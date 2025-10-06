package com.ondutylogger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ondutylogger.data.repo.OnDutyRepository
import com.ondutylogger.data.entity.OnDutyEntry
import com.ondutylogger.data.entity.Parameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: OnDutyRepository): ViewModel() {
    val parameters: StateFlow<List<Parameter>> = repository.getAllParametersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val entries: StateFlow<List<OnDutyEntry>> = repository.getAllEntriesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedParameterId = MutableStateFlow<Long?>(null)

    val filteredEntries: StateFlow<List<OnDutyEntry>> = combine(entries, selectedParameterId) { list, pid ->
        if (pid == null) list else list.filter { it.parameterId == pid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
