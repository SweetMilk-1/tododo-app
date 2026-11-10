package ru.tododo.feature.notification.impl

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.UUID

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val channelId = inputData.getString(ARG_CHANNEL_ID) ?: return Result.failure()
        val title = inputData.getInt(ARG_TITLE, -1).takeIf { it != -1 } ?: return Result.failure()
        val text = inputData.getInt(ARG_TEXT, -1).takeIf { it != -1 } ?: return Result.failure()
        val smallIcon =
            inputData.getInt(ARG_SMALL_ICON, -1).takeIf { it != -1 } ?: return Result.failure()
        val taskId = inputData.getString(ARG_TASK_ID) ?: return Result.failure()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(ContextCompat.getString(applicationContext, title))
            .setContentText(ContextCompat.getString(applicationContext, text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        if (applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(11, notification)
        }
        return Result.success()
    }

    companion object {

        fun createParams(
            notification: Notification,
            taskId: UUID
        ) = workDataOf(
            ARG_CHANNEL_ID to notification.channelType.id,
            ARG_TITLE to notification.title,
            ARG_TEXT to notification.text,
            ARG_SMALL_ICON to notification.smallIcon,
            ARG_TASK_ID to taskId.toString()
        )

        private const val ARG_CHANNEL_ID = "ARG_CHANNEL_ID"
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_TEXT = "ARG_TEXT"
        private const val ARG_SMALL_ICON = "ARG_SMALL_ICON"
        private const val ARG_TASK_ID = "ARG_TASK_ID"
    }
}