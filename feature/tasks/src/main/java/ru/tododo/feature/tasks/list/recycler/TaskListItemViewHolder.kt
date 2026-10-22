package ru.tododo.feature.tasks.list.recycler

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tododo.data.dto.Task

internal class TaskListItemViewHolder(private val textView: TextView): RecyclerView.ViewHolder(textView) {

    fun bind(task: Task) {
        textView.text = task.title
    }
}