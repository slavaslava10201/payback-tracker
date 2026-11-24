package com.example.paybacktracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paybacktracker.data.model.ProjectWithStats
import com.example.paybacktracker.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProjectListUiState(
    val isCreateDialogOpen: Boolean = false,
    val projectToRename: ProjectWithStats? = null,
    val projectToDelete: ProjectWithStats? = null
)

class ProjectListViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<ProjectWithStats>> = repository.allProjectsWithStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()

    fun openCreateDialog() {
        _uiState.value = _uiState.value.copy(isCreateDialogOpen = true)
    }

    fun dismissCreateDialog() {
        _uiState.value = _uiState.value.copy(isCreateDialogOpen = false)
    }

    fun createProject(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            repository.createProject(trimmed)
            dismissCreateDialog()
        }
    }

    fun openRenameDialog(project: ProjectWithStats) {
        _uiState.value = _uiState.value.copy(projectToRename = project)
    }

    fun dismissRenameDialog() {
        _uiState.value = _uiState.value.copy(projectToRename = null)
    }

    fun renameProject(projectId: Long, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            repository.renameProject(projectId, trimmed)
            dismissRenameDialog()
        }
    }

    fun openDeleteDialog(project: ProjectWithStats) {
        _uiState.value = _uiState.value.copy(projectToDelete = project)
    }

    fun dismissDeleteDialog() {
        _uiState.value = _uiState.value.copy(projectToDelete = null)
    }

    fun confirmDelete() {
        val project = _uiState.value.projectToDelete ?: return
        viewModelScope.launch {
            repository.deleteProject(project.id)
            dismissDeleteDialog()
        }
    }
}
