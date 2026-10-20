package ru.tododo.feature.tasks.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.tododo.core.utils.errorLog
import ru.tododo.data.dto.Priority
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.TaskRepository
import ru.tododo.feature.tasks.R
import ru.tododo.future.Future
import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class TaskDetailsUiState(
    val isEditing: Boolean = false,
    val title: String = "",
    val description: String = "",
    val dueDate: Date? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val titleError: Int? = null,
    val isDirty: Boolean = false,
    val savedEvent: Task? = null,
    val deletedEvent: Unit? = null,
)

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableLiveData(TaskDetailsUiState())
    val state: LiveData<TaskDetailsUiState> = _state

    private var taskId: UUID? = null

    fun setDueDate(date: Date) = mutate { copy(dueDate = date) }
    fun setPriority(p: Priority) = mutate { copy(priority = p) }
    fun setCompleted(value: Boolean) = mutate { copy(isCompleted = value) }

    fun save() {

    }

    fun delete() {

    }

    private fun mutate(block: TaskDetailsUiState.() -> TaskDetailsUiState) {
        val current = _state.value ?: return
        _state.value = current.copy(isDirty = true).block()
    }
}