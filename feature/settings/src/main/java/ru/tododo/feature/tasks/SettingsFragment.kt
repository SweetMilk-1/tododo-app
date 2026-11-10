package ru.tododo.feature.tasks

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.tododo.feature.settings.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}