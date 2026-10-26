package ru.tododo.feature.tasks.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.tododo.data.dto.Priority
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.TaskRepository
import ru.tododo.feature.tasks.R
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class TaskDetailsViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailsUiState.DEFAULT)
    val state: StateFlow<TaskDetailsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<TaskDetailsEvents>()
    val events: SharedFlow<TaskDetailsEvents> = _events.asSharedFlow()

    private var task: Task? = null
        set(value) {
            field = value
            value?.let { updateUiStateWithTask(it) }
        }

    fun init(
        taskId: UUID?
    ) {
        taskId?.let {
            startObserve(taskId)
        }
    }

    fun setTitle(title: String) = mutate { copy(title = title, titleErrorRes = null) }
    fun setDescription(description: String) = mutate { copy(description = description) }
    fun setDueDate(date: Long) = mutate { copy(dueDate = date) }
    fun setPriority(p: Priority) = mutate { copy(priority = p) }
    fun setCompleted(value: Boolean) = mutate { copy(isCompleted = value) }

    fun save() {
        if (!ensureFormValid()) return
        val freshTask = getTaskFromUiState()
        if (task == null) {
            insertNewTaskAndStartObserve()
        } else {
            updateTask(freshTask)
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            task?.id?.let { taskRepository.delete(it) }
            _events.emit(TaskDetailsEvents.BackToList)
        }
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        val result = taskRepository.update(task)
        when {
            result.isSuccess -> _events.emit(TaskDetailsEvents.BackToList)
            result.isFailure -> handleUpdateError(result.exceptionOrNull())
        }
    }

    private fun handleUpdateError(exceptionOrNull: Throwable?) {
        TODO("Not yet implemented")
    }

    private fun insertNewTaskAndStartObserve() = viewModelScope.launch {
        val task = getTaskFromUiState()
        val result = taskRepository.insert(task)
        when {
            result.isSuccess -> _events.emit(TaskDetailsEvents.BackToList)
            result.isFailure -> handleInsertError(result.exceptionOrNull())
        }
    }

    private fun handleInsertError(exceptionOrNull: Throwable?) {
        TODO("Not yet implemented")
    }

    private fun getTaskFromUiState(): Task {
        val state = _state.value
        return Task(
            id = task?.id,
            title = state.title,
            description = state.description,
            dueDate = state.dueDate,
            priority = state.priority,
            isCompleted = state.isCompleted
        )
    }

    private fun updateUiStateWithTask(task: Task) {
        mutate {
            copy(
                fragmentTitleRes = R.string.edit_task_title,
                isEditing = true,
                title = task.title,
                titleErrorRes = null,
                description = task.description,
                dueDate = task.dueDate,
                priority = task.priority,
                isCompleted = task.isCompleted,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt
            )
        }
    }

    private fun startObserve(taskId: UUID) {
        viewModelScope.launch {
            taskRepository.observeById(taskId).also {
                it.collect {
                    task = it
                }
            }
        }
    }

    private fun ensureFormValid(): Boolean {
        val uiState = state.value
        if (uiState.title.isBlank()) {
            mutate { copy(titleErrorRes = R.string.error_title_required) }
            return false
        }
        if (uiState.title.length < MIN_TITLE_LENGTH) {
            mutate { copy(titleErrorRes = R.string.error_title_too_short) }
            return false
        }
        return true
    }

    private fun mutate(block: TaskDetailsUiState.() -> TaskDetailsUiState) {
        val current = _state.value
        _state.value = current.block()
    }

    fun openDeleteDialog() {
        viewModelScope.launch {
            _events.emit(TaskDetailsEvents.OpenDeleteDialog)
        }
    }

    companion object {
        private const val MIN_TITLE_LENGTH = 5
    }
}