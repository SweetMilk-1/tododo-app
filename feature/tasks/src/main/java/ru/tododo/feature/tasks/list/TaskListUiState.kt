package ru.tododo.feature.tasks.list

import java.util.UUID

data class TaskListUiState(
    val taskList: List<TaskItem> = emptyList()
) {

    companion object {
        val DEFAULT = TaskListUiState()
    }
}

data class TaskItem(
    val id: UUID,
    val title: String,
    val dueDate: Long?,
    val isCompleted: Boolean
)