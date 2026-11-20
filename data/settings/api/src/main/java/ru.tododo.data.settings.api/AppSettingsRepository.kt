package ru.tododo.data.settings.api

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {

    fun observeSettings(): Flow<AppSettings>

    suspend fun getSettings(): AppSettings

    suspend fun update(block: (AppSettings) -> AppSettings)
}