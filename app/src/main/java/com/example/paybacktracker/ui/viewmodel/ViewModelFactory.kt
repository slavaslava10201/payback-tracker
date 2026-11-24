package com.example.paybacktracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.paybacktracker.data.repository.ProjectRepository
import com.example.paybacktracker.data.repository.SettingsRepository

class ViewModelFactory(
    private val repository: ProjectRepository,
    private val settingsRepository: SettingsRepository? = null,
    private val projectId: Long = -1L
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProjectListViewModel::class.java) -> {
                ProjectListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProjectDetailViewModel::class.java) -> {
                if (projectId == -1L) {
                    throw IllegalArgumentException("projectId must be provided for ProjectDetailViewModel")
                }
                ProjectDetailViewModel(projectId, repository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                if (settingsRepository == null) {
                    throw IllegalArgumentException("settingsRepository must be provided for SettingsViewModel")
                }
                SettingsViewModel(settingsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
