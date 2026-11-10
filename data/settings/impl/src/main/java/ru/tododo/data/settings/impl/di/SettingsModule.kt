package ru.tododo.data.settings.impl.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tododo.data.settings.api.AppSettings
import ru.tododo.data.settings.impl.AppSettingsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {

    @Provides
    @Singleton
    fun getSettings(@ApplicationContext context: Context): AppSettings = AppSettingsImpl(context)
}