package ru.tododo.core.room

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import ru.tododo.data.repository.room.converters.DateConverters
import ru.tododo.data.repository.room.dao.TaskDao
import ru.tododo.data.repository.room.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = true
)
@ColumnTypeConverters(DateConverters::class)
abstract class TododoDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "tododo_database"
    }
}