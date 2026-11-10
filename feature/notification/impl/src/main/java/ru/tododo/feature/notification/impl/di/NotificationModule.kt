package ru.tododo.feature.notification.impl.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tododo.data.settings.api.AppSettings
import ru.tododo.feature.notification.api.TododoNotificationManager
import ru.tododo.feature.notification.impl.TododoNotificationManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {

    @Provides
    @Singleton
    fun provideTododoNotificationManager(
        @ApplicationContext context: Context,
        appSettings: AppSettings
    ): TododoNotificationManager = TododoNotificationManagerImpl(context, appSettings)
}