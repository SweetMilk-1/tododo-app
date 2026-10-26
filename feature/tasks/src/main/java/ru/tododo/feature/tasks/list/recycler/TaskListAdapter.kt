package ru.tododo.feature.tasks.list.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.tododo.feature.tasks.databinding.ViewItemTaskBinding
import ru.tododo.feature.tasks.list.TaskItem


internal class TaskListAdapter(
    private val layoutInflater: LayoutInflater,
    private val taskItemInteractListener: TaskItemInteractListener,
) : ListAdapter<TaskItem, TaskListItemViewHolder>(TaskDiffUtilCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskListItemViewHolder {
        val viewItemTaskBinding = createItemViewBinding(parent)
        return TaskListItemViewHolder(viewItemTaskBinding, taskItemInteractListener)
    }

    private fun createItemViewBinding(parent: ViewGroup) =
        ViewItemTaskBinding.inflate(layoutInflater, parent, false)

    override fun onBindViewHolder(
        holder: TaskListItemViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item)
    }
}