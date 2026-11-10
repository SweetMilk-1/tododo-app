package ru.tododo.feature.notification.api

import ru.tododo.data.dto.Task

interface TododoNotificationManager {

    fun scheduleTaskDueDateNotification(
        task: Task
    )
}