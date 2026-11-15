package ru.tododo.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.tododo.core.notifications.api.NotificationService
import ru.tododo.feature.notifications.api.TaskNotificationCoordinator
import javax.inject.Inject

@HiltAndroidApp
class TododoApplication: Application() {

    @Inject lateinit var taskNotificationCoordinator: TaskNotificationCoordinator
    @Inject lateinit var notificationService: NotificationService

    override fun onCreate() {
        super.onCreate()
        taskNotificationCoordinator.start()
        notificationService.initChannels()
    }
}