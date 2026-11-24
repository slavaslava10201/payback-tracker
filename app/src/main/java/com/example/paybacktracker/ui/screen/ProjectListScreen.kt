package com.example.paybacktracker.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paybacktracker.data.model.ProjectWithStats
import com.example.paybacktracker.ui.components.AddEditProjectDialog
import com.example.paybacktracker.ui.components.BalanceBadge
import com.example.paybacktracker.ui.components.DeleteConfirmationDialog
import com.example.paybacktracker.ui.theme.BorderColor
import com.example.paybacktracker.ui.theme.GreenPrimary
import com.example.paybacktracker.ui.theme.RedPrimary
import com.example.paybacktracker.ui.theme.Slate400
import com.example.paybacktracker.ui.theme.Slate500
import com.example.paybacktracker.ui.theme.Slate600
import com.example.paybacktracker.ui.theme.Slate900
import com.example.paybacktracker.ui.viewmodel.ProjectListViewModel
import com.example.paybacktracker.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel,
    onProjectClick: (projectId: Long) -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Проекты",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
                containerColor = Slate900,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Создать проект",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (projects.isEmpty()) {
                EmptyProjectsState(
                    onAddClick = { viewModel.openCreateDialog() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(projects, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            onClick = { onProjectClick(project.id) },
                            onRename = { viewModel.openRenameDialog(project) },
                            onDelete = { viewModel.openDeleteDialog(project) }
                        )
                    }
                    // Bottom spacing for FAB
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Create Project Dialog
    if (uiState.isCreateDialogOpen) {
        AddEditProjectDialog(
            title = "Новый проект",
            confirmButtonText = "Создать",
            onDismiss = { viewModel.dismissCreateDialog() },
            onConfirm = { name -> viewModel.createProject(name) }
        )
    }

    // Rename Project Dialog
    uiState.projectToRename?.let { project ->
        AddEditProjectDialog(
            initialName = project.name,
            title = "Переименовать проект",
            confirmButtonText = "Сохранить",
            onDismiss = { viewModel.dismissRenameDialog() },
            onConfirm = { newName -> viewModel.renameProject(project.id, newName) }
        )
    }

    // Delete Confirmation Dialog
    uiState.projectToDelete?.let { project ->
        DeleteConfirmationDialog(
            title = "Удалить «${project.name}»?",
            message = "Все сохранённые суммы (доходы и расходы) по этому проекту будут удалены без возможности восстановления.",
            confirmButtonText = "Удалить",
            onDismiss = { viewModel.dismissDeleteDialog() },
            onConfirm = { viewModel.confirmDelete() }
        )
    }
}

@Composable
fun ProjectCard(
    project: ProjectWithStats,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Name and Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Действия",
                            tint = Slate400
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Переименовать") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            },
                            onClick = {
                                menuExpanded = false
                                onRename()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Удалить", color = RedPrimary) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = RedPrimary
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Balance section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Баланс",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500
                    )
                    Text(
                        text = CurrencyFormatter.formatBalance(project.balance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (project.balance >= 0) GreenPrimary else RedPrimary
                    )
                }

                BalanceBadge(balance = project.balance)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Spent and Received breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Потрачено: ${CurrencyFormatter.formatAmount(project.totalExpense)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate600
                )
                Text(
                    text = "Получено: ${CurrencyFormatter.formatAmount(project.totalIncome)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate600
                )
            }
        }
    }
}

@Composable
fun EmptyProjectsState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Slate400
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет проектов",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Нажмите «+», чтобы создать первый проект и начать вести учёт окупаемости.",
            style = MaterialTheme.typography.bodyMedium,
            color = Slate500,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
