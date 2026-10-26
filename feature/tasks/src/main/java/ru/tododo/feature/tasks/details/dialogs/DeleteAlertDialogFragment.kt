package ru.tododo.feature.tasks.details.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.tododo.feature.tasks.R
import ru.tododo.core.resources.R as CoreR

private typealias OnDeleteApprovedCallback = () -> Unit

class DeleteAlertDialogFragment: DialogFragment() {

    private var callback: OnDeleteApprovedCallback? = null

    fun setOnDeleteApprovedCallback(callback: OnDeleteApprovedCallback) {
        this.callback = callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_task)
            .setMessage(R.string.delete_task_question)
            .setNegativeButton(CoreR.string.cancel) { _, _ ->
                dismiss()
            }
            .setPositiveButton(CoreR.string.yes) {_, _ ->
                callback?.invoke()
                dismiss()
            }.create()
    }
}