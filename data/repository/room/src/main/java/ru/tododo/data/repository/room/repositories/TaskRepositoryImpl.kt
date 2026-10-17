package ru.tododo.data.repository.room.repositories

import kotlinx.coroutines.flow.StateFlow
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.TaskRepository
import ru.tododo.data.repository.room.dao.TaskDao
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
): TaskRepository {

    override fun observeTasks(): StateFlow<Collection<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): Result<Collection<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: UUID): Result<Task?> {
        TODO("Not yet implemented")
    }

    override suspend fun update(updatedTask: Task): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(task: Task): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UUID): Result<Unit> {
        TODO("Not yet implemented")
    }
}