package ru.tododo.feature.tasks.list.recycler

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.tododo.feature.tasks.R
import ru.tododo.feature.tasks.details.colorRes
import ru.tododo.feature.tasks.list.TaskItem
import ru.tododo.feature.tasks.databinding.ViewItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class TaskListItemViewHolder(
    private val viewBinding: ViewItemTaskBinding,
    private val taskItemInteractListener: TaskItemInteractListener
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

        // Цветовая индикация приоритета
        val priorityColor = ContextCompat.getColor(context, taskItem.priority.colorRes())
        viewBinding.priorityDot.backgroundTintList = ColorStateList.valueOf(priorityColor)

        // Завершённая задача: приглушение + зачёркивание
        val strikeFlag = if (taskItem.isCompleted) Paint.STRIKE_THRU_TEXT_FLAG else 0
        val alpha = if (taskItem.isCompleted) COMPLETED_ALPHA else 1f
        viewBinding.taskTitle.paintFlags =
            (viewBinding.taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()) or strikeFlag
        viewBinding.dueDate.paintFlags =
            (viewBinding.dueDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()) or strikeFlag
        viewBinding.taskTitle.alpha = alpha
        viewBinding.dueDate.alpha = alpha
    }

    fun swipe() {
        taskItem?.let { taskItemInteractListener.deleteTask(it.id) }
    }

    companion object {
        private const val COMPLETED_ALPHA = 0.55f

        @SuppressLint("ConstantLocale")
        private val DATE_TIME_FORMATTER = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    }

}
