package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VibeDao {
    @Query("SELECT * FROM vibe_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<VibeProject>>

    @Query("SELECT * FROM vibe_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Int): VibeProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VibeProject): Long

    @Update
    suspend fun updateProject(project: VibeProject)

    @Delete
    suspend fun deleteProject(project: VibeProject)

    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY fileName ASC")
    fun getFilesByProject(projectId: Int): Flow<List<ProjectFile>>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId AND fileName = :fileName LIMIT 1")
    suspend fun getFileByName(projectId: Int, fileName: String): ProjectFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFile): Long

    @Update
    suspend fun updateFile(file: ProjectFile)

    @Delete
    suspend fun deleteFile(file: ProjectFile)

    @Query("SELECT * FROM vibe_messages WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getMessagesByProject(projectId: Int): Flow<List<VibeMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: VibeMessage): Long

    @Query("DELETE FROM vibe_messages WHERE projectId = :projectId")
    suspend fun clearMessagesForProject(projectId: Int)
}
