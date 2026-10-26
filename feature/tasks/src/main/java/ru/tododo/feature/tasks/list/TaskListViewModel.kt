package ru.tododo.feature.tasks.list

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.TaskRepository
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
internal class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
    private var tasks = emptyList<Task>()
        set(value) {
            field = value
            _state.value = _state.value.copy(taskList = value.map { it.mapToTaskItem() })
        }

    private val _state = MutableStateFlow(TaskListUiState.DEFAULT)
    val state: StateFlow<TaskListUiState> get() = _state

    private val _events = MutableSharedFlow<TaskListEvent>()
    val events: SharedFlow<TaskListEvent> get() = _events

    init {
        viewModelScope.launch {
            taskRepository.observeAll().collect {
                tasks = it.toList()
            }
        }
    }

    fun openTaskDetails(taskId: UUID) {
        viewModelScope.launch {
            _events.emit(TaskListEvent.OpenTaskDetails(taskId))
        }
    }

    fun setTaskCompleted(taskId: UUID, isCompleted: Boolean) {
        viewModelScope.launch {
            val task = tasks.firstOrNull{it.id == taskId}?.copy(isCompleted = isCompleted) ?: run {
                _events.emit(TaskListEvent.Error("Wrong operation"))
                return@launch
            }
            taskRepository.update(task)
        }
    }

    fun deleteTask(taskId: UUID) {
        viewModelScope.launch {
            taskRepository.delete(taskId)
        }
    }

    fun addNewTask() {
        viewModelScope.launch {
            _events.emit(TaskListEvent.OpenNewTaskForm)
        }
    }

    fun showSnackbar(@StringRes msgId: Int) {
        viewModelScope.launch {
            _events.emit(TaskListEvent.ShowSnackBar(msgId))
        }
    }

    private fun Task.mapToTaskItem (): TaskItem = TaskItem(
        id = this.id!!, // expected
        title = title,
        dueDate = dueDate,
        isCompleted = isCompleted
    )
}