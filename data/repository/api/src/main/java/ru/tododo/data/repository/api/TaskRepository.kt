package ru.tododo.data.repository.api

import kotlinx.coroutines.flow.StateFlow
import ru.tododo.data.dto.Task
import java.util.UUID

interface TaskRepository {

    fun listFlow(): StateFlow<Collection<Task>>

    suspend fun getById(id: UUID): Task?

    suspend fun update(updatedTask: Task)

    suspend fun delete(task: Task)

    suspend fun delete(id: UUID)
}