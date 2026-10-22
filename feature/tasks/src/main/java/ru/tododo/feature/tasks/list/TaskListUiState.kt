package ru.tododo.feature.tasks.list

import ru.tododo.data.dto.Task

data class TaskListUiState(
    val taskList: List<Task> = emptyList()
) {

    companion object {
        val DEFAULT = TaskListUiState()
    }
}