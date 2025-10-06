package com.ondutylogger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ondutylogger.data.entity.Parameter
import com.ondutylogger.data.repo.OnDutyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ParameterViewModel(private val repository: OnDutyRepository): ViewModel() {
    val parameters: StateFlow<List<Parameter>> = repository.getAllParametersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(name: String, unit: String?, position: String?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addParameter(Parameter(name = name.trim(), unit = unit?.ifBlank { null }, position = position?.ifBlank { null }))
        }
    }

    fun delete(parameter: Parameter) {
        viewModelScope.launch { repository.deleteParameter(parameter) }
    }
}
