package com.example.paybacktracker.ui.navigation

sealed class Screen(val route: String) {
    object ProjectList : Screen("project_list")
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: Long): String = "project_detail/$projectId"
    }
}
