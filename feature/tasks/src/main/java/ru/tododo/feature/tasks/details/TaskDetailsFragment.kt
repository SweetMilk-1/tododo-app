package ru.tododo.feature.tasks.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.tododo.core.utils.widget.setTextChangedListener
import ru.tododo.core.utils.widget.setTextKeepCursor
import ru.tododo.data.dto.Priority
import ru.tododo.feature.tasks.R
import ru.tododo.feature.tasks.databinding.FragmentTaskDetailsBinding
import ru.tododo.feature.tasks.details.dialogs.PriorityPickerDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import android.R as androidR

@AndroidEntryPoint
class TaskDetailsFragment : Fragment(R.layout.fragment_task_details) {

    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailsViewModel by viewModels()

    private val args: TaskDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId =  args.taskId
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

        setupListeners()
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.titleEditText.setTextChangedListener {viewModel.setTitle(it.toString())}
        binding.descriptionEditText.setTextChangedListener {viewModel.setDescription(it.toString())}
        binding.dueDateRow.setOnClickListener { showDateTimePicker() }
        binding.priorityRow.setOnClickListener { showPriorityPicker() }
        binding.saveButton.setOnClickListener { viewModel.save() }

        binding.completedSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setCompleted(isChecked)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.titleEditText.setTextKeepCursor(state.title)
                binding.titleInputLayout.error = state.titleError?.let { requireContext().getString(it) }
                binding.descriptionEditText.setTextKeepCursor(state.description)

                binding.dueValueText.text = state.dueDate
                    ?.let { DATE_TIME_FORMATTER.format(it) }
                    ?: getString(R.string.task_due_not_set)

                binding.priorityValueText.text = state.priority.getString(requireContext())

                // Switch «Выполнено» — только при редактировании
                binding.completedSwitch.isVisible = state.isEditing
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
            }
        }
    }

    private fun showDateTimePicker() {
        val current = viewModel.state.value.dueDate ?: defaultDueDate()
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
        datePicker.show(childFragmentManager, DATE_PICKER_TAG)
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
            val result = Calendar.getInstance().apply {
                timeInMillis = baseMillis
                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            viewModel.setDueDate(result.timeInMillis)
        }
        timePicker.show(childFragmentManager, TIME_PICKER_TAG)
    }

    private fun showPriorityPicker() {
        val state = viewModel.state.value
        val priorityPicker = PriorityPickerDialogFragment.Builder()
            .setDefaultPriority(state.priority)
            .build()

        priorityPicker.setOnItemSelectedListener {
            viewModel.setPriority(it)
        }
        priorityPicker.show(childFragmentManager, PRIORITY_PICKER_TAG)
    }

    private fun defaultDueDate(): Long = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, 1)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis


    companion object {
        private const val DATE_PICKER_TAG = "DATE_PICKER_TAG"
        private const val TIME_PICKER_TAG = "TIME_PICKER_TAG"
        private const val PRIORITY_PICKER_TAG = "PRIORITY_PICKER_TAG"
        @SuppressLint("ConstantLocale")
        private val DATE_TIME_FORMATTER = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    }
}