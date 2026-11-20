package ru.tododo.feature.notifications.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.tododo.core.notifications.api.TaskNotificationScheduler
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.TaskRepository
import ru.tododo.data.settings.api.AppSettings
import ru.tododo.data.settings.api.AppSettingsRepository
import ru.tododo.feature.notifications.api.TaskNotificationCoordinator
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class TaskNotificationCoordinatorImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val taskRepository: TaskRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val notificationScheduler: TaskNotificationScheduler
) : TaskNotificationCoordinator {

    private val Context.scheduledTaskDataStore: DataStore<Preferences> by preferencesDataStore(name = SHARED_PREFERENCES_NAME)
    private val scope =
        CoroutineScope(SupervisorJob() + CoroutineName("TaskNotificationCoordinatorImpl") + Dispatchers.Main)

    private lateinit var appSettings: StateFlow<AppSettings>
    private lateinit var tasks: StateFlow<List<Task>>

    override fun start() {
        scope.launch {

            appSettings = appSettingsRepository
                .observeSettings()
                .stateIn(scope = scope)

            tasks = taskRepository
                .observeAll(TaskRepository.FilterType.ACTIVE)
                .map { it.filter(::isNotificationTimeValid) }
                .onEach { synchronizeNotifications(appSettings.value, it) }
                .stateIn(scope)

            launch {
                appSettings.collect {
                    synchronizeNotifications(it, tasks.value)
                }
            }
        }
    }

    private fun isNotificationTimeValid(task: Task): Boolean =
        (task.dueDate?.let { dueDate -> dueDate - appSettings.value.notificationTimeMins.minutes.inWholeMilliseconds > System.currentTimeMillis() }
            ?: false)

    private suspend fun synchronizeNotifications(appSettings: AppSettings, tasks: List<Task>) {
        val scheduledTime = appContext.scheduledTaskDataStore.data.first()[intPreferencesKey(SCHEDULED_TIME_KEY)] ?: 0
        if (!appSettings.isNotificationEnabled && scheduledTime == 0 )
            return // No-op

        if (!appSettings.isNotificationEnabled) {
            cancelNotifications()
            return
        }

        scheduleNotifications(tasks, appSettings.notificationTimeMins)
    }

    private suspend fun cancelNotifications() {
        appContext.scheduledTaskDataStore.updateData { preferences ->
            val mutablePreferences = preferences.toMutablePreferences()

            preferences.asMap().keys.map(Preferences.Key<*>::name)
                .filter { it != SCHEDULED_TIME_KEY }
                .forEach {
                    val key = longPreferencesKey(it)
                    notificationScheduler.cancel(UUID.fromString(it))
                    mutablePreferences.remove(key)
                }

            mutablePreferences[intPreferencesKey(SCHEDULED_TIME_KEY)] = 0

            mutablePreferences
        }
    }

    private suspend fun scheduleNotifications(tasks: List<Task>, notificationTimeMins: Int) {
        appContext.scheduledTaskDataStore.updateData { preferences ->
            val mutablePreferences = preferences.toMutablePreferences()

            val handledTaskIds = mutableListOf<String>()
            tasks.forEach {
                val id = it.id ?: return@forEach
                val title = it.title
                val dueDate = it.dueDate ?: return@forEach
                val notificationTime =
                    dueDate - notificationTimeMins.minutes.inWholeMilliseconds

                val key = longPreferencesKey(id.toString())

                val scheduledTIme = mutablePreferences[key]

                if (notificationTime < System.currentTimeMillis())
                    return@forEach

                when {
                    scheduledTIme == null -> {
                        notificationScheduler.schedule(
                            taskId = id,
                            taskTitle = title,
                            time = notificationTime
                        )
                        mutablePreferences[key] = notificationTime
                    }

                    scheduledTIme != notificationTime -> {
                        notificationScheduler.schedule(
                            taskId = id,
                            taskTitle = title,
                            time = notificationTime,
                            isRescheduling = true
                        )
                        mutablePreferences[key] = notificationTime
                    }
                }
                handledTaskIds.add(id.toString())
            }

            preferences.asMap().keys.map(Preferences.Key<*>::name)
                .filter { it != SCHEDULED_TIME_KEY }
                .subtract(handledTaskIds.toSet())
                .forEach {
                    val key = longPreferencesKey(it)
                    notificationScheduler.cancel(UUID.fromString(it))
                    mutablePreferences.remove(key)
                }

            mutablePreferences[intPreferencesKey(SCHEDULED_TIME_KEY)] = notificationTimeMins

            mutablePreferences
        }
    }

    companion object {

        private const val SCHEDULED_TIME_KEY = "__scheduled_time_key"

        private const val SHARED_PREFERENCES_NAME = "task_notification_storage"
    }
}