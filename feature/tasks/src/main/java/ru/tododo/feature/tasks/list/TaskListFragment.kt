package ru.tododo.feature.tasks.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.tododo.core.utils.errorLog
import ru.tododo.data.dto.Task
import ru.tododo.feature.tasks.R
import ru.tododo.feature.tasks.databinding.FragmentTaskListBinding
import ru.tododo.feature.tasks.list.recycler.TaskItemInteractListener
import ru.tododo.feature.tasks.list.recycler.TaskListAdapter
import java.util.UUID

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()

    private val adapter by lazy {
        TaskListAdapter(layoutInflater, object : TaskItemInteractListener {
            override fun openTaskDetails(taskId: UUID) = viewModel.openTaskDetails(taskId)

            override fun deleteTask(taskId: UUID) = TODO("Not yet implemented")

            override fun setTaskCompleted(taskId: UUID, isComplete: Boolean) =
                viewModel.setTaskCompleted(taskId, isComplete)
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
    }

    private fun navigateToTaskDetails(taskId: UUID) {
        val action = TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(
            taskId = taskId.toString()
        )
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        binding.taskList.adapter = adapter
        binding.taskList.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    adapter.submitList(state.taskList)
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
            is TaskListEvent.Error -> errorLog(event.msg)
        }
    }

    private fun setupViewListeners() {
        // TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}