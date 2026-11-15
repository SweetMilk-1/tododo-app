package ru.tododo.core.notifications.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import ru.tododo.core.notifications.api.NotificationService
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class TaskNotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent?.getStringExtra(EXTRA_TASK_ID)?.let(UUID::fromString) ?: return
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: return
        showNotification(taskId, taskTitle)
    }

    private fun showNotification(taskId: UUID, taskTitle: String) {
        notificationService.postNotification(
            NotificationService.Notification.TaskExpiringNotification(
                taskId = taskId,
                taskTitle = taskTitle
            )
        )
    }

    companion object {
        const val EXTRA_TASK_ID = "ARG_TASK_ID"
        const val EXTRA_TASK_TITLE = "ARG_TASK_TITLE"

        fun createIntent(context: Context, taskId: UUID, taskTitle: String? = null) =
            Intent(context, TaskNotificationBroadcastReceiver::class.java).apply {
                putExtra(EXTRA_TASK_ID, taskId.toString())
                putExtra(EXTRA_TASK_TITLE, taskTitle)
            }
    }
}