package ru.tododo.data.settings.api

interface AppSettings {

    val isNotificationEnabled: Boolean

    val notificationTimeMins: Int
}