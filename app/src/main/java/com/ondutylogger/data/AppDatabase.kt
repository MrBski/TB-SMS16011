package com.ondutylogger.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ondutylogger.data.dao.OnDutyEntryDao
import com.ondutylogger.data.dao.ParameterDao
import com.ondutylogger.data.entity.OnDutyEntry
import com.ondutylogger.data.entity.Parameter

@Database(
    entities = [Parameter::class, OnDutyEntry::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun parameterDao(): ParameterDao
    abstract fun onDutyEntryDao(): OnDutyEntryDao

    companion object {
        fun build(context: Context): AppDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "onduty.db"
        ).fallbackToDestructiveMigration().build()
    }
}
