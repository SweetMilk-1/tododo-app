package ru.tododo.feature.tasks.details

import androidx.annotation.StringRes
import ru.tododo.data.dto.Priority
import java.util.Date

internal data class TaskDetailsUiState(
    val isEditing: Boolean = false,
    val title: String = "",
    @StringRes val titleError: Int? = null,
    val description: String? = null,
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
) {
    companion object {

        val DEFAULT = TaskDetailsUiState()
    }
}