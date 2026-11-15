package ru.tododo.core.notifications.api

import java.util.UUID


interface TaskNotificationScheduler {
    fun cancel(taskId: UUID)
    fun schedule(taskId: UUID, taskTitle: String, time: Long, isRescheduling: Boolean = false)
}