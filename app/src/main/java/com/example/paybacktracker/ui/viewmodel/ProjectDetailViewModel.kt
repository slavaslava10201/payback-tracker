package com.example.paybacktracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paybacktracker.data.entity.TransactionEntity
import com.example.paybacktracker.data.entity.TransactionType
import com.example.paybacktracker.data.model.ProjectWithStats
import com.example.paybacktracker.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProjectDetailUiState(
    val addTransactionType: TransactionType? = null,
    val isRenameDialogOpen: Boolean = false,
    val isDeleteDialogOpen: Boolean = false
)

class ProjectDetailViewModel(
    val projectId: Long,
    private val repository: ProjectRepository
) : ViewModel() {

    val project: StateFlow<ProjectWithStats?> = repository.getProjectWithStats(projectId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val transactions: StateFlow<List<TransactionEntity>> = repository.getTransactionsForProject(projectId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    fun openAddExpenseDialog() {
        _uiState.value = _uiState.value.copy(addTransactionType = TransactionType.EXPENSE)
    }

    fun openAddIncomeDialog() {
        _uiState.value = _uiState.value.copy(addTransactionType = TransactionType.INCOME)
    }

    fun dismissAddTransactionDialog() {
        _uiState.value = _uiState.value.copy(addTransactionType = null)
    }

    fun addTransaction(amount: Double, type: TransactionType) {
        if (amount <= 0) return
        viewModelScope.launch {
            repository.addTransaction(projectId, amount, type)
            dismissAddTransactionDialog()
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
        }
    }

    fun openRenameDialog() {
        _uiState.value = _uiState.value.copy(isRenameDialogOpen = true)
    }

    fun dismissRenameDialog() {
        _uiState.value = _uiState.value.copy(isRenameDialogOpen = false)
    }

    fun renameProject(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            repository.renameProject(projectId, trimmed)
            dismissRenameDialog()
        }
    }

    fun openDeleteDialog() {
        _uiState.value = _uiState.value.copy(isDeleteDialogOpen = true)
    }

    fun dismissDeleteDialog() {
        _uiState.value = _uiState.value.copy(isDeleteDialogOpen = false)
    }

    fun confirmDelete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            dismissDeleteDialog()
            onDeleted()
        }
    }
}
