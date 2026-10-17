package ru.tododo.data.repository.room.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String? = null,
    val dueDate: Date,
    val priority: Int,
    val isCompleted: Boolean = false,
    val createdAt: Date,
    val updatedAt: Date,
    val reminderTime: Date?,
)