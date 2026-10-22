package ru.tododo.feature.tasks.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.tododo.data.repository.api.TaskRepository
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _state = MutableStateFlow(TaskListUiState.DEFAULT)
    val state: StateFlow<TaskListUiState> get() = _state

    init {
        viewModelScope.launch {
            taskRepository.observeAll().collect {
                _state.value = _state.value.copy(taskList = it.toList())
            }
        }
    }
}