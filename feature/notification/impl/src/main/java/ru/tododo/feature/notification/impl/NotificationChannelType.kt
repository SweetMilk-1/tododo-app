package ru.tododo.feature.notification.impl

import android.app.NotificationManager
import androidx.annotation.StringRes

enum class NotificationChannelType(
    val id: String,
    @StringRes val channelName: Int,
    @StringRes val description: Int,
    val importance: Int
) {

    TaskExpiringChannel(
        "task_expiring_channel",
        R.string.task_expiring_channel_name,
        R.string.task_expiring_channel_description,
        NotificationManager.IMPORTANCE_DEFAULT
    )
}