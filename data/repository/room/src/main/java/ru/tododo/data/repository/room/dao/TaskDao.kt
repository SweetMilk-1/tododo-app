package ru.tododo.data.repository.room.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import ru.tododo.data.repository.room.entity.TaskEntity
import java.util.UUID

@Dao
interface TaskDao {

    @Query("SELECT * FROM task ORDER BY isCompleted, priority DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE isCompleted = 0 ORDER BY priority DESC")
    fun observeActive(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE dueDate BETWEEN :start AND :end ORDER BY isCompleted, priority DESC")
    fun observeByDueDate(start: Long, end: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task")
    suspend fun getAll(): List<TaskEntity>

    @Query("SELECT * FROM task where id = :id LIMIT 1")
    fun observeById(id: UUID): Flow<TaskEntity?>

    @Query("SELECT * FROM task where id = :id LIMIT 1")
    suspend fun getById(id: UUID): TaskEntity?

    @Upsert
    suspend fun upsert(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}