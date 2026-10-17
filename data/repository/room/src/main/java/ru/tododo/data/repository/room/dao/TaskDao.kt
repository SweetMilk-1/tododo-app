package ru.tododo.data.repository.room.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import ru.tododo.data.repository.room.entity.TaskEntity
import java.util.UUID

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task")
    suspend fun getAll(): List<TaskEntity>

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: UUID)
}