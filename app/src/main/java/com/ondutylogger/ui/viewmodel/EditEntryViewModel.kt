package com.ondutylogger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ondutylogger.data.entity.OnDutyEntry
import com.ondutylogger.data.entity.Parameter
import com.ondutylogger.data.repo.OnDutyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditEntryViewModel(private val repository: OnDutyRepository) : ViewModel() {
    val parameters: StateFlow<List<Parameter>> = repository.getAllParametersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timestamp = MutableStateFlow(System.currentTimeMillis())
    val selectedParameter = MutableStateFlow<Parameter?>(null)
    val valueText = MutableStateFlow("")
    val note = MutableStateFlow("")

    fun save(onSaved: () -> Unit) {
        val v = valueText.value.toDoubleOrNull()
        val p = selectedParameter.value
        if (v != null && p != null) {
            viewModelScope.launch {
                repository.addEntry(
                    OnDutyEntry(
                        timestamp = timestamp.value,
                        parameterId = p.id,
                        value = v,
                        note = note.value.ifBlank { null }
                    )
                )
                onSaved()
            }
        }
    }
}
