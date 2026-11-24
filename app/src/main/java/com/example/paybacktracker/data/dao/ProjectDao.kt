package com.example.paybacktracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.paybacktracker.data.entity.ProjectEntity
import com.example.paybacktracker.data.model.ProjectWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("""
        SELECT 
            p.id,
            p.name,
            p.createdAt,
            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0.0 END), 0.0) AS totalExpense,
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0.0 END), 0.0) AS totalIncome
        FROM projects p
        LEFT JOIN transactions t ON p.id = t.projectId
        GROUP BY p.id
        ORDER BY p.createdAt DESC
    """)
    fun getAllProjectsWithStats(): Flow<List<ProjectWithStats>>

    @Query("""
        SELECT 
            p.id,
            p.name,
            p.createdAt,
            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0.0 END), 0.0) AS totalExpense,
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0.0 END), 0.0) AS totalIncome
        FROM projects p
        LEFT JOIN transactions t ON p.id = t.projectId
        WHERE p.id = :projectId
        GROUP BY p.id
    """)
    fun getProjectWithStatsById(projectId: Long): Flow<ProjectWithStats?>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("UPDATE projects SET name = :newName WHERE id = :projectId")
    suspend fun renameProject(projectId: Long, newName: String)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Long)
}
