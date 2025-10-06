package com.ondutylogger.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ondutylogger.OnDutyApp
import com.ondutylogger.data.entity.OnDutyEntry
import com.ondutylogger.data.entity.Parameter
import com.ondutylogger.ui.Destinations
import com.ondutylogger.ui.viewmodel.HomeViewModel
import com.ondutylogger.ui.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as OnDutyApp
    val scope = rememberCoroutineScope()
    val vm: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = HomeViewModelFactory(app))

    LaunchedEffect(Unit) { app.appContainer.repository.prepopulateIfNeeded() }

    val entries by vm.filteredEntries.collectAsState()
    val parameters by vm.parameters.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                app.appContainer.repository.importCsv(uri)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Destinations.EditEntry.route) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ParameterFilter(parameters = parameters, selected = vm.selectedParameterId.collectAsState().value) { vm.selectedParameterId.value = it }
                Row {
                    TextButton(onClick = { launcher.launch(arrayOf("text/*","text/comma-separated-values","text/csv","application/csv")) }) {
                        Text("Import CSV")
                    }
                    TextButton(onClick = {
                        scope.launch {
                            val uri = app.appContainer.repository.exportCsv()
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(share, "Share CSV"))
                        }
                    }) { Text("Export") }
                }
            }

            LazyColumn(Modifier.fillMaxSize()) {
                items(entries) { e ->
                    val param = parameters.firstOrNull { it.id == e.parameterId }
                    EntryRow(e, param)
                }
            }
        }
    }
}

@Composable
private fun ParameterFilter(
    parameters: List<Parameter>,
    selected: Long?,
    onSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = parameters.firstOrNull { it.id == selected }?.name ?: "All"
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text(label) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("All") }, onClick = { onSelected(null); expanded = false })
            parameters.forEach { p ->
                DropdownMenuItem(text = { Text(p.name) }, onClick = { onSelected(p.id); expanded = false })
            }
        }
    }
}

@Composable
private fun EntryRow(entry: OnDutyEntry, parameter: Parameter?) {
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.timestamp), ZoneId.systemDefault())
    val ts = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dt)
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(ts, fontWeight = FontWeight.Bold)
        Spacer(Modifier.padding(2.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(parameter?.name ?: "-")
            Text(entry.value.toString())
        }
        entry.note?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        Divider(Modifier.padding(top = 8.dp))
    }
}
