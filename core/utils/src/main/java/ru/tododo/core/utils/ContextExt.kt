package ru.tododo.core.utils

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(@StringRes msgId: Int) {
    Snackbar.make(this, msgId, Snackbar.LENGTH_SHORT).show()
}

fun View.showSnackbarWithAction(
    @StringRes msgId: Int,
    @StringRes actionLabel: Int,
    duration: Int = Snackbar.LENGTH_LONG,
    onAction: () -> Unit
) {
    Snackbar.make(this, msgId, duration)
        .setAction(actionLabel) { onAction() }
        .show()
}