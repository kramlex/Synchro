package ru.nsu.synchro.msecd.sexp

data class IntAtom(
    val value: Int,
) : NumAtom {
    constructor(string: String) : this(value = string.toIntOrNull() ?: throw NumberFormatException())

    override val intValue: Int get() = value

    override fun eq(string: String): Boolean = false
    override fun eq(int: Int): Boolean = value == int
    override fun eq(expression: SExp): Boolean = if (expression is IntAtom) expression.value == value else false

    override fun toString(): String = value.toString()
}
