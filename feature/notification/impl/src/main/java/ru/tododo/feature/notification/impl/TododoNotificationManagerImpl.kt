package ru.tododo.feature.notification.impl

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ru.tododo.data.settings.api.AppSettings
import ru.tododo.feature.notification.api.TododoNotificationManager
import ru.tododo.feature.notification.impl.NotificationWorker.Companion.createParams
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TododoNotificationManagerImpl(
    private val appContext: Context,
    private val appSettings: AppSettings
) : TododoNotificationManager {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannels(
                NotificationChannelType.entries.map {
                    NotificationChannel(
                        it.id,
                        ContextCompat.getString(appContext, it.channelName),
                        it.importance
                    ).apply {
                        description =
                            ContextCompat.getString(appContext, it.description)
                    }
                }
            )
        }
    }

    override fun scheduleTaskExpiringNotification(taskId: UUID, dueDate: Long) {
        val notificationTimeMillis = appSettings.notificationTimeMins.minutes.inWholeMilliseconds
        val currentTime = System.currentTimeMillis()

        if (dueDate - notificationTimeMillis > currentTime) {
            val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(createParams(Notification.TaskExpiring, taskId))
                .setInitialDelay(5.seconds.inWholeSeconds, TimeUnit.SECONDS)
                .build()

            WorkManager
                .getInstance(appContext)
                .enqueue(notificationWorkRequest)
        }
    }
}