package ru.tododo.data.dto

import java.util.UUID

data class Task(
    val id: UUID? = null,
    val title: String,
    val description: String?,
    val dueDate: Long?,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)

