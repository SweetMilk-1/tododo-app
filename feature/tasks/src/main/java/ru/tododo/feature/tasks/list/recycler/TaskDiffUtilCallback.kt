package ru.tododo.feature.tasks.list.recycler

import androidx.recyclerview.widget.DiffUtil
import ru.tododo.data.dto.Task
import ru.tododo.feature.tasks.list.TaskItem

internal object TaskDiffUtilCallback: DiffUtil.ItemCallback<TaskItem>() {
    override fun areItemsTheSame(
        oldItem: TaskItem,
        newItem: TaskItem
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: TaskItem,
        newItem: TaskItem
    ): Boolean = oldItem == newItem
}