package ru.nsu.synchro.app.machine.runtime

data class Debugger(
    val enabled: Boolean,
    val prefix: String = "",
    val nestLevel: Int = 0
) {
    private val indent = buildString {
        repeat(nestLevel) {
            append(" ")
        }
        append(prefix)
    }

    fun println(any: Any?) {
        if (enabled) kotlin.io.println("$any".prependIndent(indent))
    }

    fun nested(prefix: String = ""): Debugger = copy(prefix = prefix, nestLevel = nestLevel + 1)
}
