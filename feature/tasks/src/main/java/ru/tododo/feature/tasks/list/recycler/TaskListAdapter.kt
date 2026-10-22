package ru.tododo.feature.tasks.list.recycler

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import ru.tododo.data.dto.Task

internal typealias OnTaskClickCallback = (Task) -> Unit

internal class TaskListAdapter(
    private val callback: OnTaskClickCallback
): ListAdapter<Task, TaskListItemViewHolder>(TaskDiffUtilCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskListItemViewHolder {
        val textView: TextView = createItemView(parent.context)
        return TaskListItemViewHolder(textView, callback)
    }

    private fun createItemView(context: Context): TextView = TextView(context).apply {
        this.layoutParams = ViewGroup.MarginLayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
        ).apply {
            marginStart = 16
            marginEnd = 16
            topMargin = 16
            bottomMargin = 16
        }
    }

    override fun onBindViewHolder(
        holder: TaskListItemViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(task = item)
    }
}