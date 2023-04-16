package ru.nsu.synchro.app.machine.dsl

data class ReturnedValue<T>(
    val conditionName: String,
    val then: T?,
    val otherwise: T?,
) {
    class Builder<T>(private val name: String) {
        var then: T? = null
        var otherwise: T? = null

        fun build(): ReturnedValue<T> = ReturnedValue(name, then, otherwise)
    }
}
