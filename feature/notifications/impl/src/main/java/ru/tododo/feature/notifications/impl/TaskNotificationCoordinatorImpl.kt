package ru.tododo.feature.notifications.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.tododo.core.notifications.api.TaskNotificationScheduler
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.TaskRepository
import ru.tododo.data.settings.api.AppSettings
import ru.tododo.feature.notifications.api.TaskNotificationCoordinator
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.forEach
import kotlin.time.Duration.Companion.minutes

class TaskNotificationCoordinatorImpl @Inject constructor(
    @ApplicationContext appContext: Context,
    private val taskRepository: TaskRepository,
    private val appSettings: AppSettings,
    private val notificationScheduler: TaskNotificationScheduler
) : TaskNotificationCoordinator {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val scheduledTasksInfoStorage = ScheduledTasksInfoStorage(appContext)

    override fun start() {
        scope.launch {
            taskRepository
                .observeAll(TaskRepository.FilterType.ACTIVE)
                .map {
                    it.filter(::isNotificationTimeValid)
                }
                .stateIn(scope)
                .collect(::synchronizeNotifications)
        }
    }

    private fun isNotificationTimeValid(task: Task): Boolean =
        (task.dueDate?.let { dueDate -> dueDate - appSettings.notificationTimeMins.minutes.inWholeMilliseconds > System.currentTimeMillis() }
            ?: false)

    private fun synchronizeNotifications(tasks: List<Task>) {
        val handledTaskIds = mutableListOf<UUID>()
        tasks.forEach {
            val id = it.id ?: return@forEach
            val title = it.title
            val dueDate = it.dueDate ?: return@forEach
            val notificationTime =
                dueDate - appSettings.notificationTimeMins.minutes.inWholeMilliseconds

            val scheduledTIme = scheduledTasksInfoStorage[id]

            if (notificationTime < System.currentTimeMillis())
                return@forEach

            when {
                scheduledTIme == null -> {
                    notificationScheduler.schedule(
                        taskId = id,
                        taskTitle = title,
                        time = notificationTime
                    )
                    scheduledTasksInfoStorage[id] = notificationTime
                }

                scheduledTIme != notificationTime -> {
                    notificationScheduler.schedule(
                        taskId = id,
                        taskTitle = title,
                        time = notificationTime,
                        isRescheduling = true
                    )
                    scheduledTasksInfoStorage[id] = notificationTime
                }
            }
            handledTaskIds.add(id)
        }

        scheduledTasksInfoStorage
            .getAllKeys()
            .subtract(handledTaskIds.toSet())
            .forEach {
                notificationScheduler.cancel(it)
                scheduledTasksInfoStorage[it] = null
            }
    }
}