package ru.nsu.synchro.msecd.sexp

sealed interface SExp {

    val tryInt: Int? get() = try { intValue } catch (e: Throwable) { null }
    val tryString: String? get() = try { stringValue } catch (e: Throwable) { null }

    @get:Throws(SExpException::class)
    open val intValue: Int get() = throw SExpException("intValue() -- IntAtom expected.")

    @get:Throws(SExpException::class)
    open val stringValue: String get() = throw SExpException("stringValue() -- SymAtom expected.")

    @get:Throws(SExpException::class)
    open val car: SExp get() = throw SExpException("car() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val cdr: SExp get() = throw SExpException("cdr() -- Cons expected. int value = $tryInt, sym = $tryString")

    @get:Throws(SExpException::class)
    open val caar: SExp get() = throw SExpException("caar() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val cadr: SExp get() = throw SExpException("cadr() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val cdar: SExp get() = throw SExpException("cdar() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val cddr: SExp get() = throw SExpException("cddr() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val caadr: SExp get() = throw SExpException("caadr() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val caddr: SExp get() = throw SExpException("caddr() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val cdadr: SExp get() = throw SExpException("cdadr() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val cdddr: SExp get() = throw SExpException("cdddr() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val cadddr: SExp get() = throw SExpException("cadddr() -- Cons expected.")

    @get:Throws(SExpException::class)
    open val cddddr: SExp get() = throw SExpException("cddddr() -- Cons expected.")

    @Throws(SExpException::class)
    fun eq(string: String): Boolean = throw SExpException("eq() -- concrete SExp expected.")

    @Throws(SExpException::class)
    fun eq(int: Int): Boolean = throw SExpException("eq() -- concrete SExp expected.")

    @Throws(SExpException::class)
    fun eq(expression: SExp): Boolean = throw SExpException("eq() -- concrete SExp expected.")

    @Throws(SExpException::class)
    fun rplaca(carSExp: SExp): SExp = throw SExpException("rplaca() -- Cons expected.")
}
