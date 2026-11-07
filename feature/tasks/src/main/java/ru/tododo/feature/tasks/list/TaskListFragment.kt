package ru.tododo.feature.tasks.list

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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ru.tododo.core.utils.errorLog
import ru.tododo.core.utils.showSnackbar
import ru.tododo.core.utils.showSnackbarWithAction
import ru.tododo.data.dto.Task
import ru.tododo.feature.tasks.R
import ru.tododo.feature.tasks.databinding.FragmentTaskListBinding
import ru.tododo.feature.tasks.details.TaskDetailsFragment.Companion.SHOW_SNACKBAR_MSG_KEY
import ru.tododo.feature.tasks.list.recycler.TaskItemInteractListener
import ru.tododo.feature.tasks.list.viewpager.TaskListViewPagerAdapter
import java.util.UUID

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()

    private val taskItemInteractListener: TaskItemInteractListener by lazy {
        object : TaskItemInteractListener {
            override fun openTaskDetails(taskId: UUID) {
                viewModel.openTaskDetails(taskId)
            }

            override fun deleteTask(taskId: UUID) {
                viewModel.deleteTask(taskId)
            }

            override fun setTaskCompleted(taskId: UUID, isComplete: Boolean) {
                viewModel.setTaskCompleted(taskId, isComplete)
            }
        }
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

        setupViewPagerWithTabBar()
        setupViewListeners()
        observeState()
        listenEvent()
        listenSnackbar()
    }

    private fun setupViewPagerWithTabBar() {
        val adapter = TaskListViewPagerAdapter(
            layoutInflater = layoutInflater,
            pages = viewModel.pages,
            taskItemInteractListener = taskItemInteractListener,
            coroutineScope = viewLifecycleOwner.lifecycleScope
        )
        binding.taskListViewPager.adapter = adapter

        binding.taskListViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val tab = TaskListFilterTab.entries[position]
                viewModel.selectTab(tab)
            }
        })

        TabLayoutMediator(binding.filterTabLayout, binding.taskListViewPager) { tab, position ->
            tab.text = ContextCompat.getString(
                requireContext(),
                adapter.getTitle(position)
            )
        }.attach()
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

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.filterTabLayout.also {
                        val tab = it.getTabAt(state.tabPosition)
                        it.selectTab(tab)
                    }
                }
            }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
