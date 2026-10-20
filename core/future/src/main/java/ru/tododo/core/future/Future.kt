package ru.tododo.future

import ru.tododo.core.utils.errorLog

fun interface SuccessAction<T> {
    fun action(res: T?)
}

fun interface FailureAction {
    fun action(errorMsg: String?)
}

class Future<T> private constructor(
    private val action: suspend () -> Result<T>,
    private val successAction: SuccessAction<T>?,
    private val failureAction: FailureAction?
) {

    suspend fun exec() {
        val result = action()
        if (result.isSuccess) {
            successAction?.action(result.getOrNull())
        } else {
            val message = result.exceptionOrNull()?.message
            errorLog(message ?: "Error")
            failureAction?.action(result.exceptionOrNull()?.message )
        }
    }

    class Builder<T>(
        private val action: suspend () -> Result<T>
    ) {

        private var successAction: SuccessAction<T>? = null
        private var failureAction: FailureAction? = null


        fun doOnSuccess(successAction: SuccessAction<T>)  = apply {
            this.successAction = successAction
        }

        fun doOnFailure(failureAction: FailureAction) = apply {
            this.failureAction = failureAction
        }

        fun build() = Future(action, successAction, failureAction)
        suspend fun buildAndExec() = build().also { it.exec() }
    }
}