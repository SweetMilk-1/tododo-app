package ru.tododo.feature.tasks.details

import androidx.annotation.StringRes
import ru.tododo.data.dto.Priority
import ru.tododo.feature.tasks.R

internal data class TaskDetailsUiState(
    @StringRes val fragmentTitleRes: Int = R.string.new_task_title,
    val isEditing: Boolean = false,
    val title: String = "",
    @StringRes val titleErrorRes: Int? = null,
    val description: String? = null,
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val isDurty: Boolean = false
) {
    companion object {

        val DEFAULT = TaskDetailsUiState()
    }
}