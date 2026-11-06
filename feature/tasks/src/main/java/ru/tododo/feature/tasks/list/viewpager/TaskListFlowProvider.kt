package ru.tododo.feature.tasks.list.viewpager

import kotlinx.coroutines.flow.Flow
import ru.tododo.feature.tasks.list.TaskItem
import ru.tododo.feature.tasks.list.TaskListFilterTab

fun interface TaskListFlowProvider {

    fun getListStateFlow(taskListFilterTab: TaskListFilterTab): Flow<List<TaskItem>>
}