package ru.tododo.core.utils

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(@StringRes msgId: Int) {
    Snackbar.make(this, msgId, Snackbar.LENGTH_SHORT).show()
}