package ru.nsu.synchro.app.machine.ast

data class ExpressionType(val name: String) {
    companion object {
        val Boolean = ExpressionType("Boolean")
        val Unit = ExpressionType("Unit")
        val NullableAny = ExpressionType("Any")
    }
}
