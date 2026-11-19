package ru.tododo.core.notifications.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.tododo.core.notifications.api.TaskNotificationScheduler
import ru.tododo.core.notifications.impl.TaskNotificationBroadcastReceiver.Companion.createIntent
import java.util.UUID
import javax.inject.Inject

class TaskNotificationSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TaskNotificationScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun cancel(taskId: UUID) {
        val intent = createIntent(context, taskId)

        val pendingIntent = getPendingIntent(taskId, intent)

        alarmManager.cancel(pendingIntent)
    }

    override fun schedule(taskId: UUID, taskTitle: String, time: Long, isRescheduling: Boolean) {
        val intent = createIntent(context, taskId, taskTitle)

        val time = System.currentTimeMillis() + 5000

        val pendingIntent = getPendingIntent(taskId, intent)

        if (isRescheduling) {
            alarmManager.cancel(pendingIntent)
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    private fun getPendingIntent(
        taskId: UUID,
        intent: Intent
    ): PendingIntent = PendingIntent.getBroadcast(
        context,
        taskId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}
