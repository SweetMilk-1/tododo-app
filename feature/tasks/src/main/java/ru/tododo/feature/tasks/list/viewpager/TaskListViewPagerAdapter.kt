package ru.tododo.feature.tasks.list.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.tododo.feature.tasks.databinding.ViewTaskListPageBinding
import ru.tododo.feature.tasks.list.PageInfo
import ru.tododo.feature.tasks.list.recycler.TaskItemInteractListener

internal class TaskListViewPagerAdapter(
    private val layoutInflater: LayoutInflater,
    private val pages: List<PageInfo>,
    private val taskItemInteractListener: TaskItemInteractListener,
    private val coroutineScope: CoroutineScope
): RecyclerView.Adapter<TaskListPageViewHolder>() {

    private val jobs = mutableMapOf<Int, Job>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskListPageViewHolder {
        val binding = ViewTaskListPageBinding.inflate(layoutInflater, parent, false)
        return TaskListPageViewHolder(
            binding = binding,
            layoutInflater = layoutInflater,
            taskItemInteractListener = taskItemInteractListener
        )
    }

    override fun onViewRecycled(holder: TaskListPageViewHolder) {
        super.onViewRecycled(holder)
        val position = holder.bindingAdapterPosition
        if (position != POSIION_UNSET) {
            jobs[position]?.cancel()?.also {
                jobs.remove(position)
            }
        }
    }

    override fun onBindViewHolder(
        holder: TaskListPageViewHolder,
        position: Int
    ) {
        val tab = pages[position]
        jobs[position] = coroutineScope.launch {
            tab.taskListFlow.stateIn(coroutineScope)
                .collect {
                    holder.bind(it)
                }
        }
    }

    override fun getItemCount(): Int = pages.size

    @StringRes fun getTitle(position: Int): Int = pages[position].filterTab.labelId

    companion object {
        val POSIION_UNSET = -1
    }
}