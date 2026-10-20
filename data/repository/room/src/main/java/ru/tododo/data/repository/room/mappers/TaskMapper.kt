package ru.tododo.data.repository.room.mappers

import ru.tododo.data.dto.Priority
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.room.entity.TaskEntity

internal fun TaskEntity.toDto(): Task = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    dueDate = this.dueDate,
    priority = Priority.parse(this.priority),
    isCompleted = this.isCompleted,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

internal fun Task.toEntity() : TaskEntity = TaskEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    dueDate = this.dueDate,
    priority = this.priority.ordinal,
    isCompleted = this.isCompleted,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)