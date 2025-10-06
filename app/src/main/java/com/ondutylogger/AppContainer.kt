package com.ondutylogger

import android.content.Context
import com.ondutylogger.data.AppDatabase
import com.ondutylogger.data.repo.OnDutyRepository
import kotlinx.coroutines.CoroutineScope

class AppContainer(context: Context, private val appScope: CoroutineScope) {
    private val db: AppDatabase = AppDatabase.build(context)
    val repository: OnDutyRepository = OnDutyRepository(context, db)
}
