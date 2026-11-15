package ru.tododo.core.notifications.impl

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import ru.tododo.core.resources.R as CoreR
import androidx.annotation.DrawableRes
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.tododo.core.notifications.api.NotificationService
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationServiceImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : NotificationService {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun initChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannels(
                NotificationService.Channel.entries
                    .map {
                        val meta = NotificationChannelMeta.createFromChannel(appContext, it)
                        NotificationChannel(
                            meta.id,
                            meta.name,
                            NotificationManager.IMPORTANCE_HIGH
                        ).apply {
                            description = meta.description
                        }
                    }
            )
        }
    }

    override fun tryRequestPermission(activity: Activity) {
        if (appContext.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            activity.requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                42
            )
            return
        }
    }

    override fun postNotification(notification: NotificationService.Notification): Boolean {
        if (appContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return false
        }

        when (notification) {
            is NotificationService.Notification.TaskExpiringNotification -> {
                val notificationMeta = NotificationMeta.createFromNotification(appContext, notification)

                val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Notification.Builder(appContext, notificationMeta.channel.id)
                } else {
                    Notification.Builder(appContext)
                }
                val androidNotification =  notificationBuilder.setContentTitle(notificationMeta.title)
                    .setContentText(notificationMeta.text)
                    .setSmallIcon(notificationMeta.icon)
                    .setAutoCancel(true)
                    .setContentIntent(createOpenTaskPendingIntent(notification.taskId))
                    .build()

                notificationManager.notify(notification.taskId.hashCode(), androidNotification)
                return true
            }
        }
    }

    private fun createOpenTaskPendingIntent(taskId: UUID): PendingIntent {
        val uri = "tododo://feature/tasks/$taskId".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(appContext.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            appContext,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

@ConsistentCopyVisibility
private data class NotificationChannelMeta private constructor(
    val channel: NotificationService.Channel,
    val id: String,
    val name: String,
    val description: String
) {

    companion object {

        fun createFromChannel(context: Context, channel: NotificationService.Channel) =
            when (channel) {
                NotificationService.Channel.TaskExpiringChannel -> NotificationChannelMeta(
                    channel = channel,
                    id = channel.name,
                    name = context.getString(R.string.task_expiring_channel_name),
                    description = context.getString(R.string.task_expiring_channel_description),
                )
            }
    }
}

@ConsistentCopyVisibility
private data class NotificationMeta private constructor(
    val channel: NotificationChannelMeta,
    val notification: NotificationService.Notification,
    val title: String,
    val text: String,
    @DrawableRes val icon: Int
) {

    companion object {

        fun createFromNotification(
            context: Context,
            notification: NotificationService.Notification
        ): NotificationMeta {
            val channel = NotificationChannelMeta.createFromChannel(context, notification.channel)
            return when (notification) {
                is NotificationService.Notification.TaskExpiringNotification -> NotificationMeta(
                    channel = channel,
                    notification = notification,
                    title = context.getString(R.string.task_expiring_notification_title),
                    text = context.getString(
                        R.string.task_expiring_notification_text,
                        notification.taskTitle
                    ),
                    icon = CoreR.drawable.ic_pace
                )
            }
        }
    }
}