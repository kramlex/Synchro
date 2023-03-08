package ru.nsu.synchro.msecd.sexp

data class Cons(
    var carSExp: SExp,
    var cdrSExp: SExp,
) : SExp {

    override fun toString(): String {
        var s = car.toString()
        var temp = cdr

        while (temp is Cons) {
            s = "$s ${temp.car}"
            temp = temp.cdr
        }
        if (temp != SymAtom.NIL) {
            s = "$s . $temp"
        }
        return "($s)"
    }
    constructor(
        int: Int,
        cdrSExp: SExp,
    ) : this(IntAtom(int), cdrSExp)

    constructor(
        string: String,
        cdrSExp: SExp,
    ) : this(SymAtom(string), cdrSExp)

    constructor(first: Int, second: Int) : this(IntAtom(first), IntAtom(second))

    override val car: SExp get() = carSExp
    override val cdr: SExp get() = cdrSExp
    override val caar: SExp get() = this.car.car
    override val cadr: SExp get() = this.cdr.car
    override val cdar: SExp get() = this.car.cdr
    override val cddr: SExp get() = this.cdr.cdr
    override val caadr: SExp get() = this.cdr.car.car
    override val caddr: SExp get() = this.cdr.cdr.car
    override val cdadr: SExp get() = this.cdr.car.cdr
    override val cdddr: SExp get() = this.cdr.cdr.cdr
    override val cadddr: SExp get() = this.cdr.cdr.cdr.car
    override val cddddr: SExp get() = this.cdr.cdr.cdr.cdr

    override fun eq(string: String): Boolean = false
    override fun eq(int: Int): Boolean = false
    override fun eq(expression: SExp): Boolean = false

    override fun rplaca(carSExp: SExp): SExp {
        this.carSExp = carSExp
        return this
    }
}