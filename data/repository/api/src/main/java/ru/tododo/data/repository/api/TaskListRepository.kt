package ru.tododo.data.repository.api

import kotlinx.coroutines.flow.StateFlow
import ru.tododo.data.dto.TaskList
import java.util.UUID

interface TaskListRepository {

    fun listFlow(): StateFlow<Collection<TaskList>>

    suspend fun getById(id: UUID): TaskList?

    suspend fun update(updatedTaskList: TaskList)

    suspend fun delete(task: TaskList)

    suspend fun delete(id: UUID)
}