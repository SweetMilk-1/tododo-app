package ru.tododo.data.settings.api

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val isNotificationEnabled: Boolean,
    val notificationTimeMins: Int
) {

    companion object {
        val DEFAULT = AppSettings(isNotificationEnabled = false, notificationTimeMins = 5)
    }
}