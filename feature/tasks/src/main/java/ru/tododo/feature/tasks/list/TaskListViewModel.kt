package ru.tododo.feature.tasks.list

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.tododo.data.dto.Task
import ru.tododo.data.repository.api.TaskRepository
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
internal class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
    private var currentTasks = emptyList<Task>()

    private val _state = MutableStateFlow(
        TaskListUiState.DEFAULT.copy(
            tabPosition = 0,
        )
    )
    val state: StateFlow<TaskListUiState> get() = _state

    private val _events = MutableSharedFlow<TaskListEvent>()
    val events: SharedFlow<TaskListEvent> get() = _events

    val pages: List<PageInfo> = TaskListFilterTab.entries.map {
        PageInfo(it, getTaskListFlowForTab(it))
    }

    fun selectTab(tab: TaskListFilterTab) {
        _state.value = _state.value.copy(tabPosition = tab.ordinal)
        viewModelScope.launch {
            taskRepository.observeAll(tab.mapToFilterType()).collect {
                currentTasks = it.toList()
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
            val task =
                currentTasks.firstOrNull { it.id == taskId }?.copy(isCompleted = isCompleted) ?: run {
                    _events.emit(TaskListEvent.Error("Wrong operation"))
                    return@launch
                }
            taskRepository.update(task)
        }
    }

    fun deleteTask(taskId: UUID) {
        viewModelScope.launch {
            val deleted = currentTasks.firstOrNull { it.id == taskId }
            taskRepository.delete(taskId)
            if (deleted != null) {
                _events.emit(TaskListEvent.ShowUndoDelete(deleted))
            }
        }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            taskRepository.restore(task)
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

    private fun getTaskListFlowForTab(tab: TaskListFilterTab): Flow<List<TaskItem>> {
        return taskRepository.observeAll(tab.mapToFilterType())
            .map { it.map { task -> task.mapToTaskItem() } }
    }

    private fun Task.mapToTaskItem(): TaskItem = TaskItem(
        id = this.id!!, // expected
        title = title,
        dueDate = dueDate,
        priority = priority,
        isCompleted = isCompleted
    )

    private fun TaskListFilterTab.mapToFilterType() =
        tabToFilterTypeMap[this] ?: throw IllegalArgumentException("Unknown filter tab $this")

    private companion object {

        private val tabToFilterTypeMap = mapOf(
            TaskListFilterTab.ALL to TaskRepository.FilterType.ALL,
            TaskListFilterTab.ACTIVE to TaskRepository.FilterType.ACTIVE,
            TaskListFilterTab.TODAY to TaskRepository.FilterType.TODAY,
            TaskListFilterTab.EXPIRED to TaskRepository.FilterType.EXPIRED
        )
    }
}