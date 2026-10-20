package ru.tododo.feature.tasks.details

import android.annotation.SuppressLint
import android.R as androidR
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import ru.tododo.data.dto.Priority
import ru.tododo.feature.tasks.R
import ru.tododo.feature.tasks.databinding.FragmentTaskDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class TaskDetailFragment : Fragment(R.layout.fragment_task_details) {

    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskEditViewModel by viewModels()

    private var taskId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskId = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupListeners()
        observeState()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ---------------------------------------------------------------- Toolbar

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener { handleBackPress() }
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_save -> {
                        viewModel.save()
                        true
                    }
                    R.id.action_delete -> {
                        confirmDelete()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupListeners() {
        binding.dueRow.setOnClickListener { showDateTimePicker() }
        binding.priorityRow.setOnClickListener { showPriorityPicker() }

        binding.completedSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setCompleted(isChecked)
        }

        binding.titleEditText.requestFocus()
    }

    // ---------------------------------------------------------------- Observe

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.titleEditText.setText(state.title)
            binding.descriptionEditText.setText(state.description)

            binding.titleInputLayout.error = state.titleError?.let(::getString)

            binding.dueValueText.text = state.dueDate
                ?.let { DATE_TIME_FORMATTER.format(it) }
                ?: getString(R.string.task_due_not_set)

            binding.priorityValueText.text = when (state.priority) {
                Priority.LOW -> getString(R.string.priority_low)
                Priority.MEDIUM -> getString(R.string.priority_medium)
                Priority.HIGH -> getString(R.string.priority_high)
            }

            // Switch «Выполнено» — только при редактировании
            binding.completedRow.isVisible = state.isEditing
            if (binding.completedSwitch.isChecked != state.isCompleted) {
                binding.completedSwitch.isChecked = state.isCompleted
            }

            // Метаданные
            binding.createdAtText.isVisible = state.createdAt != null
            binding.createdAtText.text = state.createdAt?.let {
                getString(R.string.task_created_at, DATE_TIME_FORMATTER.format(it))
            }

            binding.updatedAtText.isVisible = state.updatedAt != null
            binding.updatedAtText.text = state.updatedAt?.let {
                getString(R.string.task_updated_at, DATE_TIME_FORMATTER.format(it))
            }

            // Toolbar
            binding.toolbar.title = getString(
                if (state.isEditing) R.string.task_edit_title else R.string.task_new_title
            )
            binding.toolbar.menu.findItem(R.id.action_delete)?.isVisible = state.isEditing

            state.savedEvent?.let { task ->
                Snackbar.make(binding.root, R.string.task_saved, Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

            state.deletedEvent?.let {
                findNavController().popBackStack()
            }
        }
    }

    // ---------------------------------------------------------------- Pickers

    private fun showDateTimePicker() {
        val current = viewModel.state.value?.dueDate?.time ?: defaultDueDate()
        val cal = Calendar.getInstance().apply { timeInMillis = current }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.pick_date_title)
            .setSelection(cal.timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val picked = Calendar.getInstance().apply {
                timeInMillis = selection
                set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
            }
            showTimePicker(picked.timeInMillis)
        }
        datePicker.show(childFragmentManager, "date_picker")
    }

    private fun showTimePicker(baseMillis: Long) {
        val cal = Calendar.getInstance().apply { timeInMillis = baseMillis }
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(cal.get(Calendar.HOUR_OF_DAY))
            .setMinute(cal.get(Calendar.MINUTE))
            .setTitleText(R.string.pick_time_title)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val result = Date(
                Calendar.getInstance().apply {
                    timeInMillis = baseMillis
                    set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    set(Calendar.MINUTE, timePicker.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            )
            viewModel.setDueDate(result)
        }
        timePicker.show(childFragmentManager, "time_picker")
    }

    private fun showPriorityPicker() {
        val labels = arrayOf(
            getString(R.string.priority_low),
            getString(R.string.priority_medium),
            getString(R.string.priority_high),
        )
        val values = Priority.values()
        val selected = values.indexOf(viewModel.state.value?.priority).coerceAtLeast(0)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.task_priority_label)
            .setSingleChoiceItems(labels, selected) { dialog, which ->
                viewModel.setPriority(values[which])
                dialog.dismiss()
            }
            .setNegativeButton(androidR.string.cancel, null)
            .show()
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.task_delete_title)
            .setMessage(R.string.task_delete_message)
            .setPositiveButton(R.string.action_delete) { _, _ -> viewModel.delete() }
            .setNegativeButton(androidR.string.cancel, null)
            .show()
    }

    // ---------------------------------------------------------------- Utils

    private fun handleBackPress() {
        val state = viewModel.state.value ?: return
        val hasChanges = state.isDirty
        if (!hasChanges) {
            findNavController().popBackStack()
            return
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.discard_changes_title)
            .setMessage(R.string.discard_changes_message)
            .setPositiveButton(R.string.action_discard) { _, _ ->
                findNavController().popBackStack()
            }
            .setNegativeButton(androidR.string.cancel, null)
            .show()
    }

    private fun defaultDueDate(): Long = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, 1)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    companion object {
        private const val ARG_TASK_ID = "arg_task_id"

        fun newInstance() = TaskDetailFragment()

        @SuppressLint("ConstantLocale")
        private val DATE_TIME_FORMATTER = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    }
}