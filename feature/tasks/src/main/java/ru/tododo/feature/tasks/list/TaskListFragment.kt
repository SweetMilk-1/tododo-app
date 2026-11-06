package ru.tododo.feature.tasks.list

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R as MaterialR
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ru.tododo.core.resources.R as CoreR
import ru.tododo.core.utils.errorLog
import ru.tododo.core.utils.showSnackbar
import ru.tododo.core.utils.showSnackbarWithAction
import ru.tododo.data.dto.Task
import ru.tododo.feature.tasks.R
import ru.tododo.feature.tasks.databinding.FragmentTaskListBinding
import ru.tododo.feature.tasks.details.TaskDetailsFragment.Companion.SHOW_SNACKBAR_MSG_KEY
import ru.tododo.feature.tasks.list.recycler.TaskItemInteractListener
import ru.tododo.feature.tasks.list.recycler.TaskListAdapter
import ru.tododo.feature.tasks.list.recycler.TaskListItemViewHolder
import java.util.UUID

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()

    private val adapter by lazy {
        TaskListAdapter(layoutInflater, object : TaskItemInteractListener {
            override fun openTaskDetails(taskId: UUID) = viewModel.openTaskDetails(taskId)

            override fun deleteTask(taskId: UUID) = viewModel.deleteTask(taskId)

            override fun setTaskCompleted(taskId: UUID, isComplete: Boolean) =
                viewModel.setTaskCompleted(taskId, isComplete)
        })
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
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewListeners()
        observeState()
        listenEvent()
        listenSnackbar()
    }

    private fun listenSnackbar() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.getStateFlow<Int?>(SHOW_SNACKBAR_MSG_KEY, null)
                    ?.shareIn(
                        scope = viewLifecycleOwner.lifecycleScope,
                        started = SharingStarted.Eagerly,
                        replay = 0
                    )
                    ?.collect { msgId ->
                        msgId?.let { viewModel.showSnackbar(it) }
                    }
            }
        }
    }

    private fun navigateToTaskDetails(taskId: UUID?) {
        val action = TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(
            taskId = taskId?.toString()
        )
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        binding.taskList.adapter = adapter
        binding.taskList.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.taskList.setHasFixedSize(true)
        (binding.taskList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.taskList.addItemDecoration(
            SpacingItemDecoration(resources.getDimensionPixelSize(CoreR.dimen.list_item_spacing))
        )
        recyclerViewItemTouchHelper.attachToRecyclerView(binding.taskList)
    }

    private fun drawSwipeBackground(c: Canvas, view: View, dX: Float) {
        val context = view.context
        val background = ColorDrawable(
            MaterialColors.getColor(context, MaterialR.attr.colorErrorContainer, 0)
        )
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

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    adapter.submitList(state.taskList)
                    toggleEmptyState(state.taskList.isEmpty())
                }
            }
        }
    }

    private fun toggleEmptyState(showEmpty: Boolean) {
        val container = binding.emptyListMessageContainer
        container.animate().cancel()
        if (showEmpty) {
            if (container.visibility == View.GONE) {
                container.alpha = 0f
                container.visibility = View.VISIBLE
            }
            container.animate().alpha(1f).setDuration(EMPTY_FADE_MS).start()
        } else if (container.visibility == View.VISIBLE) {
            container.animate().alpha(0f).setDuration(EMPTY_FADE_MS)
                .withEndAction { container.visibility = View.GONE }
                .start()
        }
    }

    private fun listenEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect {
                    handleEvent(it)
                }
            }
        }
    }

    private fun handleEvent(event: TaskListEvent) {
        when (event) {
            is TaskListEvent.OpenTaskDetails -> navigateToTaskDetails(event.taskId)
            is TaskListEvent.OpenNewTaskForm -> navigateToTaskDetails(null)
            is TaskListEvent.Error -> errorLog(event.msg)
            is TaskListEvent.ShowSnackBar -> requireView().showSnackbar(event.msgId)
            is TaskListEvent.ShowUndoDelete -> showUndoDeleteSnackbar(event.task)
        }
    }

    private fun showUndoDeleteSnackbar(task: Task) {
        requireView().showSnackbarWithAction(
            R.string.task_deleted_snackbar,
            R.string.undo
        ) {
            viewModel.restoreTask(task)
        }
    }

    private fun setupViewListeners() {
        binding.addNewTaskFloatingButton.setOnClickListener {
            viewModel.addNewTask()
        }

        binding.addNewTaskButton.setOnClickListener {
            viewModel.addNewTask()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EMPTY_FADE_MS = 150L
    }
}
