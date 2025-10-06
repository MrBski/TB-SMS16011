package com.ondutylogger.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parameters"
)
data class Parameter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val unit: String? = null,
    val position: String? = null
)

@Entity(
    tableName = "on_duty_entries",
    foreignKeys = [
        ForeignKey(
            entity = Parameter::class,
            parentColumns = ["id"],
            childColumns = ["parameterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("parameterId")]
)
data class OnDutyEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val parameterId: Long,
    val value: Double,
    val note: String? = null,
    val rawText: String? = null,
    @ColumnInfo(defaultValue = "0") val createdAt: Long = System.currentTimeMillis()
)
