package ru.tododo.data.dto

data class TaskList(
    val id: String,
    val name: String,
    val color: Int,
    val createdAt: Long
)