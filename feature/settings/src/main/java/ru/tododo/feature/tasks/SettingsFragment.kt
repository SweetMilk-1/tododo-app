package ru.tododo.feature.tasks

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.tododo.data.settings.api.AppSettingsRepository
import ru.tododo.feature.settings.R
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    private lateinit var notificationEnabledPref: SwitchPreferenceCompat
    private lateinit var notificationTimePref: DropDownPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        notificationEnabledPref = findPreference(key(R.string.enable_notification_key))!!
        notificationTimePref = findPreference(key(R.string.notification_time_key))!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            val currentSettings = appSettingsRepository.getSettings()

            notificationEnabledPref.isChecked = currentSettings.isNotificationEnabled
            notificationTimePref.value = currentSettings.notificationTimeMins.toString()

            setupListeners()
        }
    }

    private fun setupListeners() {
        notificationEnabledPref.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as? Boolean ?: return@setOnPreferenceChangeListener false
            viewLifecycleOwner.lifecycleScope.launch {
                appSettingsRepository.update { appSettings ->
                    appSettings.copy(isNotificationEnabled = value)
                }
            }
            true
        }

        notificationTimePref.setOnPreferenceChangeListener { _, newValue ->
            val value =
                (newValue as? String)?.toInt() ?: return@setOnPreferenceChangeListener false
            viewLifecycleOwner.lifecycleScope.launch {
                appSettingsRepository.update { appSettings ->
                    appSettings.copy(notificationTimeMins = value)
                }
            }
            true
        }
    }

    private fun key(@StringRes resId: Int) = getString(resId)

    companion object {
        private val TAG = "SettingsFragment"
    }
}