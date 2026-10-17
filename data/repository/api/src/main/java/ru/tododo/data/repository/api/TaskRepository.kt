package ru.tododo.data.repository.api

import kotlinx.coroutines.flow.StateFlow
import ru.tododo.data.dto.Task
import java.util.UUID

interface TaskRepository {

    fun observeTasks(): StateFlow<Collection<Task>>

    suspend fun getAll(): Result<Collection<Task>>

    suspend fun getById(id: UUID): Result<Task?>

    suspend fun update(updatedTask: Task): Result<Unit>

    suspend fun delete(task: Task): Result<Unit>

    suspend fun delete(id: UUID): Result<Unit>
}