package com.ondutylogger

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class OnDutyApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this, applicationScope)
    }
}
