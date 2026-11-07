package ru.tododo.feature.tasks.list

import kotlinx.coroutines.flow.Flow
import ru.tododo.data.dto.Priority
import java.util.UUID

internal data class TaskListUiState(
    val tabPosition: Int = 0,
) {

    companion object {
        val DEFAULT = TaskListUiState()
    }
}

internal data class PageInfo(
    val filterTab: TaskListFilterTab,
    val taskListFlow: Flow<List<TaskItem>>
)

internal data class TaskItem(
    val id: UUID,
    val title: String,
    val dueDate: Long?,
    val priority: Priority,
    val isCompleted: Boolean
)