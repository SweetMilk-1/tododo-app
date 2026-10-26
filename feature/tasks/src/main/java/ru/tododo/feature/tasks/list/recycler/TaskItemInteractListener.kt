package ru.tododo.feature.tasks.list.recycler

import java.util.UUID

internal interface TaskItemInteractListener {
    fun openTaskDetails(taskId: UUID)
    fun deleteTask(taskId: UUID)
    fun setTaskCompleted(taskId: UUID, isComplete: Boolean)
}