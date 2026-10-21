package ru.tododo.data.dto

enum class Priority {
    LOW,
    MEDIUM,
    HIGH;

    companion object {

        fun parse(priorInt: Int): Priority {
            return try {
                entries[priorInt]
            } catch (_: Throwable) {
                throw IllegalArgumentException("Unknown priority: $priorInt")
            }
        }
    }
}