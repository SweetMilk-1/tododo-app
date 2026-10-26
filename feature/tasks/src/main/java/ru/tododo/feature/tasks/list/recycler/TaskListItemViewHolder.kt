package ru.tododo.feature.tasks.list.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import ru.tododo.feature.tasks.R
import ru.tododo.feature.tasks.list.TaskItem
import ru.tododo.feature.tasks.databinding.ViewItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class TaskListItemViewHolder(
    private val viewBinding: ViewItemTaskBinding,
    taskItemInteractListener: TaskItemInteractListener
) : RecyclerView.ViewHolder(viewBinding.root) {

    private var taskItem: TaskItem? = null


    init {
        viewBinding.root.setOnClickListener {
            taskItem?.let {
                taskItemInteractListener.openTaskDetails(
                    it.id
                )
            }
        }
        viewBinding.completedCheckbox.setOnCheckedChangeListener { _, value ->
            taskItem?.let { taskItemInteractListener.setTaskCompleted(it.id, value) }
        }
    }

    fun bind(taskItem: TaskItem) {
        this.taskItem = taskItem
        val context = viewBinding.root.context
        viewBinding.taskTitle.text = taskItem.title
        val dueDateText = taskItem.dueDate?.let {
            context.getString(R.string.due_date, DATE_TIME_FORMATTER.format(Date(taskItem.dueDate)))
        } ?: context.getString(R.string.no_due_date)
        viewBinding.dueDate.text = dueDateText
        viewBinding.completedCheckbox.isChecked = taskItem.isCompleted
        if (taskItem.isCompleted) {
            viewBinding.taskTitle.paintFlags =
                viewBinding.taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            viewBinding.dueDate.paintFlags =
                viewBinding.dueDate.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            viewBinding.taskTitle.paintFlags =
                viewBinding.taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            viewBinding.dueDate.paintFlags =
                viewBinding.dueDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    companion object {
        @SuppressLint("ConstantLocale")
        private val DATE_TIME_FORMATTER = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    }

}