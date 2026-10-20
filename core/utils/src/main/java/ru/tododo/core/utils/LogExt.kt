package ru.tododo.core.utils

import android.util.Log

fun Any.debugLog(msg: String) {
    Log.d(this::class.simpleName, msg)
}

fun Any.warnLog(msg: String) {
    Log.w(this::class.simpleName, msg)
}


fun Any.errorLog(msg: String) {
    Log.e(this::class.simpleName, msg)
}

