package ru.tododo.feature.tasks.list

import androidx.annotation.StringRes
import ru.tododo.data.dto.Task
import java.util.UUID

internal sealed interface TaskListEvent {

    class OpenTaskDetails(val taskId: UUID): TaskListEvent
    object OpenNewTaskForm: TaskListEvent
    class ShowSnackBar(@StringRes val msgId: Int): TaskListEvent
    class ShowUndoDelete(val task: Task): TaskListEvent
    class Error(val msg: String): TaskListEvent
}