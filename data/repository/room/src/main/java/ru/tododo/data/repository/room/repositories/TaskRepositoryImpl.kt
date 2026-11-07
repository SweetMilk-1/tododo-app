package ru.tododo.data.repository.room.repositories

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.ErrorCode
import ru.tododo.data.repository.api.TaskRepository
import ru.tododo.data.repository.api.exceptions.RepositoryException
import ru.tododo.data.repository.room.dao.TaskDao
import ru.tododo.data.repository.room.mappers.toDto
import ru.tododo.data.repository.room.mappers.toEntity
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeAll(filterType: TaskRepository.FilterType): Flow<Collection<Task>> {
        return when (filterType) {
            TaskRepository.FilterType.ALL -> taskDao.observeAll()
            TaskRepository.FilterType.ACTIVE -> taskDao.observeActive()
            TaskRepository.FilterType.TODAY -> {
                val (start, end) = getStartAndEndTimeMillisOfTheDay()
                taskDao.observeByDueDate(start, end)
            }
            TaskRepository.FilterType.EXPIRED -> {
                val (start, _) = getStartAndEndTimeMillisOfTheDay()
                taskDao.observeByDueDate(Long.MIN_VALUE, start - 1)
            }
        }.map { list -> list.map { it.toDto() } }
    }

    private fun getStartAndEndTimeMillisOfTheDay(): Pair<Long, Long> {
        var start: Long = 0
        var end: Long = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)

            start = today
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()

            end = today
                .plusDays(1)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli() - 1

        } else {
            val cal = Calendar.getInstance()

            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            start = cal.timeInMillis

            cal.add(Calendar.DAY_OF_YEAR, 1)
            end = cal.timeInMillis
        }

        return start to end
    }

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

    override suspend fun restore(task: Task): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            taskDao.upsert(task.toEntity())
            Result.success(Unit)
        } catch (th: Throwable) {
            Result.failure(th)
        }
    }
}