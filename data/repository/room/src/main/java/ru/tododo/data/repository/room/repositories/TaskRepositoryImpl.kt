package ru.tododo.data.repository.room.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.ErrorCode
import ru.tododo.data.repository.api.TaskRepository
import ru.tododo.data.repository.api.exceptions.RepositoryException
import ru.tododo.data.repository.room.dao.TaskDao
import ru.tododo.data.repository.room.mappers.toDto
import ru.tododo.data.repository.room.mappers.toEntity
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeAll(): Flow<Collection<Task>> = taskDao.observeAll().map { list -> list.map { it.toDto() } }

    override fun observeById(id: UUID): Flow<Task?> = taskDao.observeById(id).map { it?.toDto() }

    override suspend fun insert(newTask: Task): Result<UUID> = withContext(Dispatchers.IO) {
        return@withContext try {
            val id = UUID.randomUUID()
            val task = newTask.copy(
                id = id,
                isCompleted = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            taskDao.upsert(task.toEntity())
            Result.success(id)
        } catch (th: Throwable) {
            Result.failure(th)
        }
    }

    override suspend fun update(updatedTask: Task): Result<Unit> = withContext(Dispatchers.IO){
        return@withContext try {
            val taskId = updatedTask.id ?: throw IllegalArgumentException("task.id can not be null!")
            val oldTask = taskDao.getById(taskId) ?: throw RepositoryException(ErrorCode.NOT_FOUND)

            val task = updatedTask.copy(
                createdAt = oldTask.createdAt,
                updatedAt = System.currentTimeMillis()
            )
            val entity = task.toEntity()
            taskDao.upsert(entity)
            Result.success(Unit)
        } catch (th: Throwable) {
            Result.failure(th)
        }
    }

    override suspend fun delete(id: UUID): Result<Unit> {
        return try {
            val oldTask = taskDao.getById(id) ?: throw RepositoryException(ErrorCode.NOT_FOUND)
            taskDao.delete(oldTask)
            Result.success(Unit)
        } catch (th: Throwable) {
            Result.failure(th)
        }
    }
}