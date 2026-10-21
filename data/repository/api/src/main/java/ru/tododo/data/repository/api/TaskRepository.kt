package ru.tododo.data.repository.api

import kotlinx.coroutines.flow.Flow
import ru.tododo.data.dto.Task
import java.util.UUID

interface TaskRepository {

    fun observeAll(): Flow<Collection<Task>>

    fun observeById(id: UUID): Flow<Task?>

    suspend fun insert(newTask: Task): Result<UUID>

    suspend fun update(updatedTask: Task): Result<Unit>

    suspend fun delete(id: UUID): Result<Unit>
}