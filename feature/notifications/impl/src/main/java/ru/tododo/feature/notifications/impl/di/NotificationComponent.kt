package ru.tododo.feature.notifications.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.tododo.feature.notifications.api.TaskNotificationCoordinator
import ru.tododo.feature.notifications.impl.TaskNotificationCoordinatorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationComponent {

    @Binds
    @Singleton
    abstract fun bindTaskNotificationCoordinator(
        impl: TaskNotificationCoordinatorImpl
    ): TaskNotificationCoordinator
}