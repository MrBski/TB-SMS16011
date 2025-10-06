package com.ondutylogger.data.repo

import android.content.Context
import android.net.Uri
import com.ondutylogger.data.AppDatabase
import com.ondutylogger.data.dao.OnDutyEntryDao
import com.ondutylogger.data.dao.ParameterDao
import com.ondutylogger.data.entity.OnDutyEntry
import com.ondutylogger.data.entity.Parameter
import com.ondutylogger.util.CsvUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OnDutyRepository(
    private val context: Context,
    private val db: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val parameterDao: ParameterDao = db.parameterDao()
    private val entryDao: OnDutyEntryDao = db.onDutyEntryDao()

    fun getAllParametersFlow(): Flow<List<Parameter>> = parameterDao.getAllFlow()
    fun getAllEntriesFlow(): Flow<List<OnDutyEntry>> = entryDao.getAllFlow()
    fun getEntriesByParameterFlow(parameterId: Long): Flow<List<OnDutyEntry>> = entryDao.getByParameterFlow(parameterId)

    suspend fun addParameter(parameter: Parameter): Long = withContext(ioDispatcher) {
        parameterDao.insert(parameter)
    }

    suspend fun updateParameter(parameter: Parameter) = withContext(ioDispatcher) {
        parameterDao.update(parameter)
    }

    suspend fun deleteParameter(parameter: Parameter) = withContext(ioDispatcher) {
        parameterDao.delete(parameter)
    }

    suspend fun addEntry(entry: OnDutyEntry): Long = withContext(ioDispatcher) { entryDao.insert(entry) }
    suspend fun updateEntry(entry: OnDutyEntry) = withContext(ioDispatcher) { entryDao.update(entry) }
    suspend fun deleteEntry(entry: OnDutyEntry) = withContext(ioDispatcher) { entryDao.delete(entry) }

    suspend fun prepopulateIfNeeded() = withContext(ioDispatcher) {
        val shared = context.getSharedPreferences("seed", Context.MODE_PRIVATE)
        if (shared.getBoolean("seeded", false)) return@withContext
        val csv = context.assets.open("initial_data.csv").bufferedReader().use { it.readText() }
        val rows = CsvUtils.parse(csv)
        val existingParams = parameterDao.getAll().associateBy { it.name.trim() }
        val paramsToInsert = mutableListOf<Parameter>()
        val entriesToInsert = mutableListOf<OnDutyEntry>()
        val nameToId = mutableMapOf<String, Long>()

        // Insert missing parameters first
        for (row in rows) {
            val name = row.parameterName ?: continue
            if (!existingParams.containsKey(name)) {
                paramsToInsert.add(
                    Parameter(name = name, unit = row.unit, position = row.position)
                )
            }
        }
        val insertedIds = if (paramsToInsert.isNotEmpty()) parameterDao.insertAll(paramsToInsert) else emptyList()
        paramsToInsert.forEachIndexed { index, p -> nameToId[p.name] = insertedIds.getOrElse(index) { 0L } }
        existingParams.values.forEach { p -> nameToId[p.name] = p.id }

        // Build entries
        for (row in rows) {
            val name = row.parameterName ?: continue
            val pid = nameToId[name] ?: continue
            val ts = row.timestampMs ?: continue
            val value = row.value ?: continue
            entriesToInsert.add(
                OnDutyEntry(
                    timestamp = ts,
                    parameterId = pid,
                    value = value,
                    note = row.note,
                    rawText = row.rawText
                )
            )
        }
        if (entriesToInsert.isNotEmpty()) {
            entryDao.insertAll(entriesToInsert)
        }
        shared.edit().putBoolean("seeded", true).apply()
    }

    suspend fun importCsv(uri: Uri): Int = withContext(ioDispatcher) {
        val input = context.contentResolver.openInputStream(uri) ?: return@withContext 0
        val csv = input.bufferedReader().use { it.readText() }
        val rows = CsvUtils.parse(csv)
        var inserted = 0
        val existingParams = parameterDao.getAll().associateBy { it.name.trim() }.toMutableMap()
        for (row in rows) {
            val name = row.parameterName ?: continue
            val ts = row.timestampMs ?: continue
            val value = row.value ?: continue
            val unit = row.unit
            val position = row.position

            val pid = existingParams[name]?.id ?: run {
                val id = parameterDao.insert(Parameter(name = name, unit = unit, position = position))
                val p = Parameter(id = id, name = name, unit = unit, position = position)
                existingParams[name] = p
                id
            }
            entryDao.insert(
                OnDutyEntry(
                    timestamp = ts,
                    parameterId = pid,
                    value = value,
                    note = row.note,
                    rawText = row.rawText
                )
            )
            inserted++
        }
        inserted
    }

    suspend fun exportCsv(): Uri = withContext(ioDispatcher) {
        CsvUtils.exportAll(context, db)
    }
}
