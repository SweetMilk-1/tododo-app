package ru.tododo.feature.notification.api

import java.util.UUID

interface TododoNotificationManager {

    fun initNotificationChannels()
    fun scheduleTaskExpiringNotification(taskId: UUID, dueDate: Long)
}