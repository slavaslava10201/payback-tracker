package com.example.paybacktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.paybacktracker.data.model.AppLanguage
import com.example.paybacktracker.data.model.AppTheme
import com.example.paybacktracker.ui.navigation.Screen
import com.example.paybacktracker.ui.screen.ProjectDetailScreen
import com.example.paybacktracker.ui.screen.ProjectListScreen
import com.example.paybacktracker.ui.screen.SettingsScreen
import com.example.paybacktracker.ui.theme.PaybackTrackerTheme
import com.example.paybacktracker.ui.viewmodel.ProjectDetailViewModel
import com.example.paybacktracker.ui.viewmodel.ProjectListViewModel
import com.example.paybacktracker.ui.viewmodel.SettingsViewModel
import com.example.paybacktracker.ui.viewmodel.ViewModelFactory
import com.example.paybacktracker.util.AppStrings

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as PaybackApp
        val repository = app.repository
        val settingsRepository = app.settingsRepository

        setContent {
            val appTheme by settingsRepository.theme.collectAsState(initial = AppTheme.SYSTEM)
            val appLanguage by settingsRepository.language.collectAsState(initial = AppLanguage.RU)
            val strings = AppStrings.get(appLanguage)

            PaybackTrackerTheme(appTheme = appTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        repository = repository,
                        settingsRepository = settingsRepository,
                        strings = strings
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    repository: com.example.paybacktracker.data.repository.ProjectRepository,
    settingsRepository: com.example.paybacktracker.data.repository.SettingsRepository,
    strings: com.example.paybacktracker.util.StringsBundle
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ProjectList.route
    ) {
        composable(Screen.ProjectList.route) {
            val listViewModel: ProjectListViewModel = viewModel(
                factory = ViewModelFactory(repository = repository, settingsRepository = settingsRepository)
            )
            ProjectListScreen(
                viewModel = listViewModel,
                strings = strings,
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: -1L
            val detailViewModel: ProjectDetailViewModel = viewModel(
                factory = ViewModelFactory(
                    repository = repository,
                    settingsRepository = settingsRepository,
                    projectId = projectId
                )
            )
            ProjectDetailScreen(
                viewModel = detailViewModel,
                strings = strings,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = ViewModelFactory(
                    repository = repository,
                    settingsRepository = settingsRepository
                )
            )
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
