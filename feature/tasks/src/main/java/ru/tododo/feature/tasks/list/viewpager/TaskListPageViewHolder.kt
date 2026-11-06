package ru.tododo.feature.tasks.list.viewpager

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch
import ru.tododo.feature.tasks.databinding.ViewTaskListPageBinding
import ru.tododo.feature.tasks.list.recycler.TaskItemInteractListener
import ru.tododo.feature.tasks.list.recycler.TaskListAdapter
import ru.tododo.feature.tasks.list.recycler.TaskListItemViewHolder
import com.google.android.material.R as MaterialR
import ru.tododo.core.resources.R as CoreR
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.Flow
import ru.tododo.feature.tasks.list.TaskItem

internal class TaskListPageViewHolder(
    private val binding: ViewTaskListPageBinding,
    private val layoutInflater: LayoutInflater,
    private val taskItemInteractListener: TaskItemInteractListener,
    private val viewLifecycleOwner: LifecycleOwner,
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter by lazy {
        TaskListAdapter(
            layoutInflater,
            taskItemInteractListener
        )
    }

    private val recyclerViewItemTouchHelper by lazy {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                (viewHolder as TaskListItemViewHolder).swipe()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                drawSwipeBackground(c, viewHolder.itemView, dX)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })
    }

    init {
        setupRecyclerView()
    }

    fun bind(listStateFlow: Flow<List<TaskItem>>) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                listStateFlow.collect { taskList ->
                    adapter.submitList(taskList)
                    toggleEmptyState(taskList.isEmpty())
                }
            }
        }
    }

    private fun toggleEmptyState(showEmpty: Boolean) {
        val container = binding.emptyListMessageContainer
        container.animate().cancel()
        if (showEmpty) {
            if (container.isGone) {
                container.alpha = 0f
                container.visibility = View.VISIBLE
            }
            container.animate().alpha(1f).setDuration(EMPTY_FADE_MS).start()
        } else if (container.isVisible) {
            container.animate().alpha(0f).setDuration(EMPTY_FADE_MS)
                .withEndAction { container.visibility = View.GONE }
                .start()
        }
    }

    private fun setupRecyclerView() {
        binding.taskList.adapter = adapter
        binding.taskList.layoutManager = LinearLayoutManager(itemView.context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.taskList.setHasFixedSize(true)
        (binding.taskList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.taskList.addItemDecoration(
            SpacingItemDecoration(itemView.context.resources.getDimensionPixelSize(CoreR.dimen.list_item_spacing))
        )
        recyclerViewItemTouchHelper.attachToRecyclerView(binding.taskList)
    }

    private fun drawSwipeBackground(c: Canvas, view: View, dX: Float) {
        val context = view.context
        val background =
            MaterialColors.getColor(context, MaterialR.attr.colorErrorContainer, 0).toDrawable()
        background.setBounds(view.left, view.top, view.right, view.bottom)
        background.draw(c)

        val icon = ContextCompat.getDrawable(context, CoreR.drawable.ic_delete)?.mutate() ?: return
        icon.setTint(MaterialColors.getColor(context, MaterialR.attr.colorOnErrorContainer, 0))
        val iconSize = view.height / 3
        val top = view.top + (view.height - iconSize) / 2
        val margin = iconSize / 2
        if (dX > 0) {
            val left = view.left + margin
            icon.setBounds(left, top, left + iconSize, top + iconSize)
        } else {
            val right = view.right - margin
            icon.setBounds(right - iconSize, top, right, top + iconSize)
        }
        icon.draw(c)
    }

    private class SpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = spacing
        }
    }

    companion object {
        private const val EMPTY_FADE_MS = 150L
    }
}