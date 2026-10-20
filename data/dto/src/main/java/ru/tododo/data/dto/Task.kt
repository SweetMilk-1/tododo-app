package ru.tododo.data.dto

import java.util.Date
import java.util.UUID

data class Task(
    val id: UUID,
    val title: String,
    val description: String?,
    val dueDate: Date,
    val priority: Priority,
    val isCompleted: Boolean,
    val createdAt: Date,
    val updatedAt: Date,
)

