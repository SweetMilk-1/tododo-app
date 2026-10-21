package ru.tododo.core.utils.widget

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.setTextChangedListener(callback: (cs: CharSequence?) -> Unit) {
    addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(p0: Editable?) = Unit

        override fun beforeTextChanged(
            p0: CharSequence?,
            p1: Int,
            p2: Int,
            p3: Int
        ) = Unit

        override fun onTextChanged(
            p0: CharSequence?,
            p1: Int,
            p2: Int,
            p3: Int
        ) {
            callback.invoke(p0)
        }
    })
}

fun EditText.setTextKeepCursor(text: String?) {
    val currentText = this.text
    if (currentText.toString() == text) return
    val selection = selectionStart
    setText(text)
    if (selection >= 0 && !text.isNullOrEmpty()) {
        setSelection(selection.coerceAtMost(text.length))
    }
}