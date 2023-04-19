package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class Debugger(
    val enabled: Boolean,
    val prefix: String = "",
    val nestLevel: Int = 0,
    private val stackTrace: MutableStateFlow<List<String>>? = null
) {
    private val indent = buildString {
        repeat(nestLevel) {
            append(" ")
        }
        append(prefix)
    }

    fun println(any: Any?) {
        val value = "$any".prependIndent(indent)
        stackTrace?.update { it + value }
        if (enabled) kotlin.io.println(value)
    }

    fun nested(prefix: String = ""): Debugger = copy(
        prefix = prefix,
        nestLevel = nestLevel + 1,
        stackTrace = stackTrace
    )
}
