package com.example.paybacktracker.data.repository

import com.example.paybacktracker.data.dao.ProjectDao
import com.example.paybacktracker.data.dao.TransactionDao
import com.example.paybacktracker.data.entity.ProjectEntity
import com.example.paybacktracker.data.entity.TransactionEntity
import com.example.paybacktracker.data.entity.TransactionType
import com.example.paybacktracker.data.model.ProjectWithStats
import kotlinx.coroutines.flow.Flow

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val transactionDao: TransactionDao
) {
    val allProjectsWithStats: Flow<List<ProjectWithStats>> =
        projectDao.getAllProjectsWithStats()

    fun getProjectWithStats(projectId: Long): Flow<ProjectWithStats?> =
        projectDao.getProjectWithStatsById(projectId)

    fun getTransactionsForProject(projectId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsForProject(projectId)

    suspend fun createProject(name: String): Long {
        val trimmed = name.trim()
        require(trimmed.isNotEmpty()) { "Project name cannot be empty" }
        return projectDao.insertProject(ProjectEntity(name = trimmed))
    }

    suspend fun renameProject(projectId: Long, newName: String) {
        val trimmed = newName.trim()
        require(trimmed.isNotEmpty()) { "Project name cannot be empty" }
        projectDao.renameProject(projectId, trimmed)
    }

    suspend fun deleteProject(projectId: Long) {
        projectDao.deleteProjectById(projectId)
    }

    suspend fun addTransaction(projectId: Long, amount: Double, type: TransactionType): Long {
        require(amount > 0) { "Amount must be greater than zero" }
        return transactionDao.insertTransaction(
            TransactionEntity(
                projectId = projectId,
                amount = amount,
                type = type
            )
        )
    }

    suspend fun deleteTransaction(transactionId: Long) {
        transactionDao.deleteTransactionById(transactionId)
    }
}
