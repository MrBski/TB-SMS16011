package com.ondutylogger.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ondutylogger.data.entity.OnDutyEntry
import com.ondutylogger.data.entity.Parameter
import kotlinx.coroutines.flow.Flow

@Dao
interface ParameterDao {
    @Query("SELECT * FROM parameters ORDER BY name ASC")
    fun getAllFlow(): Flow<List<Parameter>>

    @Query("SELECT * FROM parameters ORDER BY name ASC")
    suspend fun getAll(): List<Parameter>

    @Query("SELECT * FROM parameters WHERE id = :id")
    suspend fun getById(id: Long): Parameter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parameter: Parameter): Long

    @Update
    suspend fun update(parameter: Parameter)

    @Delete
    suspend fun delete(parameter: Parameter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parameters: List<Parameter>): List<Long>
}

@Dao
interface OnDutyEntryDao {
    @Query("SELECT * FROM on_duty_entries ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<OnDutyEntry>>

    @Query("SELECT * FROM on_duty_entries ORDER BY timestamp DESC")
    suspend fun getAll(): List<OnDutyEntry>

    @Query("SELECT * FROM on_duty_entries WHERE parameterId = :parameterId ORDER BY timestamp DESC")
    fun getByParameterFlow(parameterId: Long): Flow<List<OnDutyEntry>>

    @Query("SELECT * FROM on_duty_entries WHERE timestamp BETWEEN :from AND :to ORDER BY timestamp DESC")
    fun getByRangeFlow(from: Long, to: Long): Flow<List<OnDutyEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: OnDutyEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<OnDutyEntry>): List<Long>

    @Update
    suspend fun update(entry: OnDutyEntry)

    @Delete
    suspend fun delete(entry: OnDutyEntry)

    @Query("DELETE FROM on_duty_entries")
    suspend fun clear()
}
