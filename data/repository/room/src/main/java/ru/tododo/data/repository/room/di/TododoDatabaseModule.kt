package ru.tododo.data.repository.room.di

import android.content.Context
import androidx.room3.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tododo.data.repository.room.TododoDatabase
import ru.tododo.data.repository.room.dao.TaskDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TododoDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): TododoDatabase {
        return Room.databaseBuilder(
            context,
            TododoDatabase::class.java,
            TododoDatabase.DATABASE_NAME,
        ).build()
    }

    @Provides
    fun provideUserDao(database: TododoDatabase): TaskDao {
        return database.taskDao()
    }
}