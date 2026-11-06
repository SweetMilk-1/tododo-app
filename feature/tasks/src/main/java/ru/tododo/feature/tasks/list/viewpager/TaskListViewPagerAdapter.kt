package ru.tododo.feature.tasks.list.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import ru.tododo.feature.tasks.databinding.ViewTaskListPageBinding
import ru.tododo.feature.tasks.list.TaskListFilterTab
import ru.tododo.feature.tasks.list.recycler.TaskItemInteractListener

internal class TaskListViewPagerAdapter(
    private val layoutInflater: LayoutInflater,
    private val tabs: List<TaskListFilterTab>,
    private val viewLifecycleOwner: LifecycleOwner,
    private val taskItemInteractListener: TaskItemInteractListener,
    private val taskListFlowProvider: TaskListFlowProvider,

): RecyclerView.Adapter<TaskListPageViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskListPageViewHolder {
        val binding = ViewTaskListPageBinding.inflate(layoutInflater, parent, false)
        return TaskListPageViewHolder(
            binding = binding,
            layoutInflater = layoutInflater,
            taskItemInteractListener = taskItemInteractListener,
            viewLifecycleOwner = viewLifecycleOwner
        )
    }

    override fun onBindViewHolder(
        holder: TaskListPageViewHolder,
        position: Int
    ) {
        val tab = tabs[position]
        val flow = taskListFlowProvider.getListStateFlow(tab)
        holder.bind(flow)
    }

    override fun getItemCount(): Int = tabs.size

    @StringRes fun getTitle(position: Int): Int = tabs[position].labelId
}