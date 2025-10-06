package com.ondutylogger.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ondutylogger.OnDutyApp
import com.ondutylogger.data.entity.Parameter
import com.ondutylogger.ui.viewmodel.ParameterViewModel
import com.ondutylogger.ui.viewmodel.ParameterViewModelFactory

@Composable
fun ParameterScreen(navController: NavController) {
    val app = (androidx.compose.ui.platform.LocalContext.current.applicationContext as OnDutyApp)
    val vm: ParameterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = ParameterViewModelFactory(app))
    val parameters by vm.parameters.collectAsState()

    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Position") }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (name.isNotBlank()) {
                    vm.add(name, unit.ifBlank { null }, position.ifBlank { null })
                    name = ""; unit = ""; position = ""
                }
            }) { Text("Add") }
        }
        LazyColumn(Modifier.fillMaxSize().padding(top = 16.dp)) {
            items(parameters) { p ->
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(p.name)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { vm.delete(p) }) { Text("Delete") }
                    }
                }
            }
        }
    }
}
