package ru.tododo.feature.tasks.list

import java.util.UUID

internal sealed interface TaskListEvent {

    class OpenTaskDetails(val taskId: UUID): TaskListEvent
    object OpenNewTaskForm: TaskListEvent
    class Error(val msg: String): TaskListEvent
}