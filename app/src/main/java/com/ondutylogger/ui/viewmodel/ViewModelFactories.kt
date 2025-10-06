package com.ondutylogger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ondutylogger.OnDutyApp

class HomeViewModelFactory(private val app: OnDutyApp): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(app.appContainer.repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class EditEntryViewModelFactory(private val app: OnDutyApp): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditEntryViewModel(app.appContainer.repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ParameterViewModelFactory(private val app: OnDutyApp): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParameterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParameterViewModel(app.appContainer.repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
