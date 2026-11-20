package ru.tododo.data.settings.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.tododo.data.settings.api.AppSettings
import ru.tododo.data.settings.api.AppSettingsRepository
import javax.inject.Inject

class AppSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : AppSettingsRepository {

    private val scope =
        CoroutineScope(Dispatchers.IO + CoroutineName("AppSettingsRepositoryImpl") + SupervisorJob())

    private val Context.appSettingsDataStore: DataStore<AppSettings> by dataStore(
        fileName = FILE_NAME,
        serializer = AppSettingsSerializer,
        scope = scope,
    )

    override fun observeSettings(): Flow<AppSettings> = appContext.appSettingsDataStore.data

    override suspend fun getSettings(): AppSettings = appContext.appSettingsDataStore.data.first()

    override suspend fun update(block: (AppSettings) -> AppSettings) {
        appContext.appSettingsDataStore.updateData(block)
    }

    companion object {

        private const val FILE_NAME = "app_settings.json"
    }
}