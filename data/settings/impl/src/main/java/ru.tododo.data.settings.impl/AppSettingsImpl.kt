package ru.tododo.data.settings.impl

import android.content.Context
import android.preference.PreferenceManager
import androidx.annotation.StringRes
import ru.tododo.data.settings.api.AppSettings
import javax.inject.Inject

class AppSettingsImpl @Inject constructor(
    private val context: Context
) : AppSettings {

    override val isNotificationEnabled: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(key(ru.tododo.data.settings.keys.R.string.enable_notification_key), true)

    override val notificationTimeMins: Int
        get() = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key(ru.tododo.data.settings.keys.R.string.notification_time_key), 5)


    private fun key(@StringRes stringId: Int): String = context.getString(stringId)
}