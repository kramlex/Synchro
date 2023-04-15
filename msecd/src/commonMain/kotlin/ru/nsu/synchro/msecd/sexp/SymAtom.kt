package ru.nsu.synchro.msecd.sexp

data class SymAtom(
    val value: String,
) : Atom {

    override fun toString(): String = value

    override val stringValue: String get() = value

    override fun eq(string: String): Boolean = value == string
    override fun eq(int: Int): Boolean = false
    override fun eq(expression: SExp): Boolean = if (expression is SymAtom) expression.value == value else false

    companion object {
        val NIL: SExp = SymAtom("NIL")
        val LAMBDA: SExp = SymAtom("lambda")
        val T: SExp = SymAtom("T")
        val F: SExp = SymAtom("F")

        val reserved: List<SExp> = listOf(
            NIL, LAMBDA, T, F
        )
    }
}
