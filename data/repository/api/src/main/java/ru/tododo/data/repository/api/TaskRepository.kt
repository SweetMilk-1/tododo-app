package ru.tododo.data.repository.api

import kotlinx.coroutines.flow.Flow
import ru.tododo.data.dto.Task
import java.util.UUID

interface TaskRepository {

    fun observeAll(filterType: FilterType): Flow<Collection<Task>>

    fun observeById(id: UUID): Flow<Task?>

    suspend fun insert(newTask: Task): Result<UUID>

    suspend fun update(updatedTask: Task): Result<Unit>

    suspend fun delete(id: UUID): Result<Unit>

    suspend fun restore(task: Task): Result<Unit>

    enum class FilterType {
        ALL,
        ACTIVE,
        TODAY,
        EXPIRED
    }
}

