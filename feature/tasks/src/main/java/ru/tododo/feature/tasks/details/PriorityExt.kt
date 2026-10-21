package ru.tododo.feature.tasks.details

import android.content.Context
import ru.tododo.data.dto.Priority
import ru.tododo.feature.tasks.R

fun Priority.getString(context: Context): String = when (this) {
    Priority.LOW -> context.getString(R.string.priority_low)
    Priority.MEDIUM -> context.getString(R.string.priority_medium)
    Priority.HIGH -> context.getString(R.string.priority_high)
}
