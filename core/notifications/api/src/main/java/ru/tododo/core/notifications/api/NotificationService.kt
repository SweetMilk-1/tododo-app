package ru.tododo.core.notifications.api

import android.app.Activity
import java.util.UUID

interface NotificationService {

    fun initChannels()

    fun postNotification(notification: Notification): Boolean

    fun tryRequestPermission(activity: Activity)

    enum class Channel {
        TaskExpiringChannel
    }

    sealed interface Notification {

        val channel: Channel

        data class TaskExpiringNotification(
            val taskId: UUID,
            val taskTitle: String
        ): Notification {
            override val channel: Channel = Channel.TaskExpiringChannel
        }
    }

}