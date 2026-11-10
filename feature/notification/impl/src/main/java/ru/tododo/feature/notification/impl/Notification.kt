package ru.tododo.feature.notification.impl

enum class Notification(
    val channelType: NotificationChannelType,
    val title: Int,
    val text: Int,
    val smallIcon: Int
) {
    TaskExpiring(
        NotificationChannelType.TaskExpiringChannel,
        R.string.task_expiring_title,
        R.string.task_expiring_description,
        ru.tododo.core.resources.R.drawable.ic_pace
    )
}