package ru.tododo.feature.notifications.impl

import android.content.Context
import androidx.core.content.edit
import java.util.UUID

internal class ScheduledTasksInfoStorage(context: Context) {

    private val pref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    operator fun set(key: UUID, value: Long?) {
        pref.edit {
            if (value != null) {
                putLong(key.toString(), value)
            } else {
                remove(key.toString())
            }
        }
    }

    operator fun get(key: UUID): Long? = pref.getLong(key.toString(), -1).takeIf { it != -1L }

    fun getAllKeys(): List<UUID> = pref.all.map { it.key.let(UUID::fromString) }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "TASK_NOTIFICATION_STORAGE"
    }
}