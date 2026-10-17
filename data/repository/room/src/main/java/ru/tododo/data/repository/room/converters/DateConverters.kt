package ru.tododo.data.repository.room.converters

import androidx.room3.ColumnTypeConverter
import java.util.Date

internal object DateConverters {
    @ColumnTypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @ColumnTypeConverter
    fun timestampToDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}