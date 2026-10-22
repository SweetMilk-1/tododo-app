package ru.tododo.feature.tasks.list.recycler

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tododo.data.dto.Task

internal class TaskListItemViewHolder(
    private val textView: TextView,
    callback: OnTaskClickCallback
) : RecyclerView.ViewHolder(textView) {

    private var task: Task? = null

    init {
        textView.setOnClickListener { task?.let { callback.invoke(it) } }
    }

    fun bind(task: Task) {
        this.task = task
        textView.text = task.title
    }
}