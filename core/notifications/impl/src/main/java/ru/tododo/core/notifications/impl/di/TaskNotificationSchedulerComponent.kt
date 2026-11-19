package ru.tododo.core.notifications.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.tododo.core.notifications.api.NotificationService
import ru.tododo.core.notifications.api.TaskNotificationScheduler
import ru.tododo.core.notifications.impl.NotificationServiceImpl
import ru.tododo.core.notifications.impl.TaskNotificationSchedulerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskNotificationSchedulerComponent {

    @Binds
    @Singleton
    abstract fun bindTaskNotificationScheduler(impl: TaskNotificationSchedulerImpl): TaskNotificationScheduler

    @Binds
    @Singleton
    abstract fun bindNotificationService(impl: NotificationServiceImpl): NotificationService
}