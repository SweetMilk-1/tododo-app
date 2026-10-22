package ru.tododo.feature.tasks.list

import ru.tododo.data.dto.Task

internal sealed interface TaskListEvent {

    class OpenTask(val task: Task): TaskListEvent
}