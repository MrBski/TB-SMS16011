package com.ondutylogger.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.ondutylogger.data.AppDatabase
import com.ondutylogger.data.entity.OnDutyEntry
import com.ondutylogger.data.entity.Parameter
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CsvUtils {
    data class Row(
        val timestampIso: String?,
        val parameterName: String?,
        val value: Double?,
        val unit: String?,
        val position: String?,
        val note: String?,
        val rawText: String?
    ) {
        val timestampMs: Long? = timestampIso?.let {
            try {
                val dt = LocalDateTime.parse(it)
                dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } catch (e: Exception) {
                null
            }
        }
    }

    private val header = listOf("timestamp","parameter_name","value","unit","position","note","rawText")

    fun parse(csv: String): List<Row> {
        val lines = csv.split('\n').filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()
        val out = mutableListOf<Row>()
        val start = if (lines.first().trim().startsWith("timestamp")) 1 else 0
        for (i in start until lines.size) {
            val cols = splitCsvLine(lines[i])
            val row = Row(
                timestampIso = cols.getOrNull(0)?.ifBlank { null },
                parameterName = cols.getOrNull(1)?.ifBlank { null },
                value = cols.getOrNull(2)?.toDoubleOrNull(),
                unit = cols.getOrNull(3)?.ifBlank { null },
                position = cols.getOrNull(4)?.ifBlank { null },
                note = cols.getOrNull(5)?.ifBlank { null },
                rawText = cols.getOrNull(6)?.ifBlank { null }
            )
            out.add(row)
        }
        return out
    }

    private fun splitCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when (c) {
                '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        sb.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                ',' -> {
                    if (inQuotes) sb.append(c) else {
                        result.add(sb.toString())
                        sb.clear()
                    }
                }
                else -> sb.append(c)
            }
            i++
        }
        result.add(sb.toString())
        return result
    }

    fun exportAll(context: Context, db: AppDatabase): Uri {
        val parameterDao = db.parameterDao()
        val entryDao = db.onDutyEntryDao()
        // Blocking calls happen on IO dispatcher by caller
        val parameters = parameterDao.getAll()
        val nameById = parameters.associateBy({ it.id }, { it.name })
        val unitByName = parameters.associateBy({ it.name }, { it.unit })
        val positionByName = parameters.associateBy({ it.name }, { it.position })
        val allEntries: List<OnDutyEntry> = entryDao.getAll()

        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val sb = StringBuilder()
        sb.append(header.joinToString(",")).append('\n')
        for (e in allEntries) {
            val name = nameById[e.parameterId] ?: ""
            val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(e.timestamp), ZoneId.systemDefault())
            sb.append(
                listOf(
                    formatter.format(dt),
                    name,
                    e.value.toString(),
                    unitByName[name] ?: "",
                    positionByName[name] ?: "",
                    e.note ?: "",
                    e.rawText ?: ""
                ).joinToString(",") { escapeCsv(it) }
            ).append('\n')
        }

        val outDir = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(outDir, "on_duty_export.csv")
        file.writeText(sb.toString())
        return FileProvider.getUriForFile(context, "com.ondutylogger.fileprovider", file)
    }

    private fun escapeCsv(value: String): String {
        val needsQuote = value.contains(',') || value.contains('"') || value.contains('\n')
        val escaped = value.replace("\"", "\"\"")
        return if (needsQuote) "\"$escaped\"" else escaped
    }
}
