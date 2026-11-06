package ru.tododo.feature.tasks.list

import androidx.annotation.StringRes
import ru.tododo.feature.tasks.R

enum class TaskListFilterTab(@StringRes val labelId: Int) {

    ALL(R.string.all_task_filter),
    ACTIVE(R.string.active_task_filter),
    TODAY(R.string.today_task_filter),
    EXPIRED(R.string.expired_task_filter)
}