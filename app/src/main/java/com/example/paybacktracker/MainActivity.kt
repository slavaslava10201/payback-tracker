package com.example.paybacktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.paybacktracker.ui.navigation.Screen
import com.example.paybacktracker.ui.screen.ProjectDetailScreen
import com.example.paybacktracker.ui.screen.ProjectListScreen
import com.example.paybacktracker.ui.theme.PaybackTrackerTheme
import com.example.paybacktracker.ui.viewmodel.ProjectDetailViewModel
import com.example.paybacktracker.ui.viewmodel.ProjectListViewModel
import com.example.paybacktracker.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as PaybackApp
        val repository = app.repository

        setContent {
            PaybackTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(repository = repository)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(repository: com.example.paybacktracker.data.repository.ProjectRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ProjectList.route
    ) {
        composable(Screen.ProjectList.route) {
            val listViewModel: ProjectListViewModel = viewModel(
                factory = ViewModelFactory(repository = repository)
            )
            ProjectListScreen(
                viewModel = listViewModel,
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
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
                factory = ViewModelFactory(repository = repository, projectId = projectId)
            )
            ProjectDetailScreen(
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
