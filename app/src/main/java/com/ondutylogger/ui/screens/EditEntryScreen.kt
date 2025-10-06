package com.ondutylogger.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ondutylogger.OnDutyApp
import com.ondutylogger.data.entity.OnDutyEntry
import com.ondutylogger.data.entity.Parameter
import com.ondutylogger.ui.viewmodel.EditEntryViewModel
import com.ondutylogger.ui.viewmodel.EditEntryViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as OnDutyApp
    val vm: EditEntryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = EditEntryViewModelFactory(app))
    val parameters by vm.parameters.collectAsState()
    val timestamp by vm.timestamp.collectAsState()
    val selectedParameter by vm.selectedParameter.collectAsState()
    val valueText by vm.valueText.collectAsState()
    val note by vm.note.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dt))
            OutlinedButton(onClick = { vm.timestamp.value = System.currentTimeMillis() }) { Text("Now") }
        }
        Spacer(Modifier.height(8.dp))
        ParameterPicker(parameters = parameters, selected = selectedParameter, onSelected = { vm.selectedParameter.value = it })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = valueText, onValueChange = { vm.valueText.value = it }, label = { Text("Value") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = note, onValueChange = { vm.note.value = it }, label = { Text("Note") })
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Cancel") }
            Spacer(Modifier.height(0.dp).weight(1f))
            Button(onClick = { vm.save { navController.popBackStack() } }) { Text("Save") }
        }
    }
}

@Composable
private fun ParameterPicker(
    parameters: List<Parameter>,
    selected: Parameter?,
    onSelected: (Parameter?) -> Unit
) {
    var expanded = remember { mutableStateOf(false) }
    val label = selected?.name ?: "Select Parameter"
    Column {
        OutlinedButton(onClick = { expanded.value = true }) { Text(label) }
        androidx.compose.material3.DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            parameters.forEach { p ->
                androidx.compose.material3.DropdownMenuItem(text = { Text(p.name) }, onClick = {
                    onSelected(p)
                    expanded.value = false
                })
            }
        }
    }
}
