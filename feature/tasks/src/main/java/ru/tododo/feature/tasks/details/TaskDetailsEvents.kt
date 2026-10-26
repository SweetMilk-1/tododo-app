package ru.tododo.feature.tasks.details

import androidx.annotation.StringRes

sealed interface TaskDetailsEvents {

    data class BackToList(@StringRes val msgId: Int?): TaskDetailsEvents
    object OpenDeleteDialog: TaskDetailsEvents
}