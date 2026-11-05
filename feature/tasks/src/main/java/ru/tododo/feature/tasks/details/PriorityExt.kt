package ru.tododo.feature.tasks.details

import android.content.Context
import androidx.annotation.ColorRes
import ru.tododo.data.dto.Priority
import ru.tododo.feature.tasks.R
import ru.tododo.core.resources.R as CoreR

fun Priority.getString(context: Context): String = when (this) {
    Priority.LOW -> context.getString(R.string.priority_low)
    Priority.MEDIUM -> context.getString(R.string.priority_medium)
    Priority.HIGH -> context.getString(R.string.priority_high)
}

@ColorRes
fun Priority.colorRes(): Int = when (this) {
    Priority.LOW -> CoreR.color.priority_low
    Priority.MEDIUM -> CoreR.color.priority_medium
    Priority.HIGH -> CoreR.color.priority_high
}
