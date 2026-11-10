package ru.tododo.feature.notification.impl

import android.content.Context
import android.util.Log
import ru.tododo.data.dto.Task
import ru.tododo.data.settings.api.AppSettings
import ru.tododo.feature.notification.api.TododoNotificationManager
import java.util.UUID

class TododoNotificationManagerImpl(
    private val context: Context,
    private val appSettings: AppSettings
): TododoNotificationManager {

    override fun scheduleTaskDueDateNotification(task: Task) {
        // TODO Нужно запланировать нотификацию для текущей таски через WorkManager
        //      При этом нужно сделать так, чтобы уведомления не дублировались при смене времени
    }
}