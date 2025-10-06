package com.ondutylogger.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ondutylogger.R
import com.ondutylogger.ui.screens.EditEntryScreen
import com.ondutylogger.ui.screens.HomeScreen
import com.ondutylogger.ui.screens.ParameterScreen
import com.ondutylogger.ui.screens.SettingsScreen
import com.ondutylogger.ui.screens.ChartScreen

sealed class Destinations(val route: String) {
    data object Home: Destinations("home")
    data object EditEntry: Destinations("edit_entry")
    data object Parameters: Destinations("parameters")
    data object Settings: Destinations("settings")
    data object Chart: Destinations("chart")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnDutyAppRoot() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "On Duty Logger") },
                actions = {
                    IconButton(onClick = { navController.navigate(Destinations.Parameters.route) }) {
                        Icon(painter = painterResource(android.R.drawable.ic_menu_manage), contentDescription = "Parameters")
                    }
                    IconButton(onClick = { navController.navigate(Destinations.Settings.route) }) {
                        Icon(painter = painterResource(android.R.drawable.ic_menu_preferences), contentDescription = "Settings")
                    }
                    IconButton(onClick = { navController.navigate(Destinations.Chart.route) }) {
                        Icon(painter = painterResource(android.R.drawable.ic_menu_today), contentDescription = "Charts")
                    }
                }
            )
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = Destinations.Home.route) {
            composable(Destinations.Home.route) { HomeScreen(navController) }
            composable(Destinations.EditEntry.route) { EditEntryScreen(navController) }
            composable(Destinations.Parameters.route) { ParameterScreen(navController) }
            composable(Destinations.Settings.route) { SettingsScreen(navController) }
            composable(Destinations.Chart.route) { ChartScreen(navController) }
        }
    }
}
