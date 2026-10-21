package ru.tododo.feature.tasks.details.dialogs

import android.R as androidR
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.tododo.data.dto.Priority
import ru.tododo.feature.tasks.R
import ru.tododo.feature.tasks.details.getString

private typealias OnItemSelectedListener = (Priority) -> Unit

class PriorityPickerDialogFragment : DialogFragment() {

    private var listener: OnItemSelectedListener? = null

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val labels = Priority.entries.map { it.getString(requireContext()) }.toTypedArray()
        val checkedItem = arguments?.getInt(PRIORITY_ARG) ?: 0
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.task_priority_label)
            .setSingleChoiceItems(labels, checkedItem) { dialog, which ->
                listener?.invoke(Priority.parse(which))
                dialog.dismiss()
            }
            .setNegativeButton(androidR.string.cancel, null)
            .create()
    }

    class Builder {

        private var defaultPriority: Priority = Priority.MEDIUM

        fun setDefaultPriority(defaultPriority: Priority) = apply {
            this.defaultPriority = defaultPriority
        }

        fun build() = PriorityPickerDialogFragment().apply {
            arguments = bundleOf().apply {
                putInt(PRIORITY_ARG, defaultPriority.ordinal)
            }
        }
    }

    companion object {

        private const val PRIORITY_ARG = "PRIORITY_ARG"
    }
}