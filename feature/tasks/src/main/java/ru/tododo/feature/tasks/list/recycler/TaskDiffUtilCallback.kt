package ru.tododo.feature.tasks.list.recycler

import androidx.recyclerview.widget.DiffUtil
import ru.tododo.data.dto.Task

internal object TaskDiffUtilCallback: DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(
        oldItem: Task,
        newItem: Task
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Task,
        newItem: Task
    ): Boolean = oldItem == newItem
}