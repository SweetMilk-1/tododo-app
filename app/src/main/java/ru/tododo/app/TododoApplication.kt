package ru.tododo.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.tododo.feature.notification.api.TododoNotificationManager
import javax.inject.Inject

@HiltAndroidApp
class TododoApplication: Application() {

    @Inject lateinit var tododoNotificationManager: TododoNotificationManager

    override fun onCreate() {
        super.onCreate()
        tododoNotificationManager.initNotificationChannels()
    }
}