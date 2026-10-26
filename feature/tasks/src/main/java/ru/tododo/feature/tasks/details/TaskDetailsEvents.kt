package ru.tododo.feature.tasks.details

sealed interface TaskDetailsEvents {

    object BackToList: TaskDetailsEvents
}