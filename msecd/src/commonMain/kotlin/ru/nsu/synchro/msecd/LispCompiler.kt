package ru.nsu.synchro.msecd

import ru.nsu.synchro.msecd.sexp.Atom
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExp
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

internal object LispCompiler {

    fun compile(e: SExp): SExp {
        val worker = Worker()
        return worker.comp(e, SymAtom.NIL, Cons(4, Cons(21, SymAtom.NIL)))
    }

    class Worker {
        private var errorCount: Int = 0
        fun comp(expression: SExp, namelist: SExp, code: SExp): SExp {
            try {
                return when {
                    expression is Atom ->
                        Cons(1, Cons(location(expression, namelist), code))

                    expression.car.eq(QUOTE) ->
                        Cons(2, Cons(expression.cadr, code))

                    expression.car.eq(EQ) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = comp(
                                expression = expression.caddr,
                                namelist = namelist,
                                code = Cons(14, code),
                            ),
                        )

                    expression.car.eq(ADD) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = comp(
                                expression = expression.caddr,
                                namelist = namelist,
                                code = Cons(15, code),
                            ),
                        )

                    expression.car.eq(SUB) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = comp(
                                expression = expression.caddr,
                                namelist = namelist,
                                code = Cons(16, code),
                            ),
                        )

                    expression.car.eq(MUL) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = comp(
                                expression = expression.caddr,
                                namelist = namelist,
                                code = Cons(17, code),
                            ),
                        )

                    expression.car.eq(DIV) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = comp(
                                expression = expression.caddr,
                                namelist = namelist,
                                code = Cons(18, code),
                            ),
                        )

                    expression.car.eq(REM) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = comp(
                                expression = expression.caddr,
                                namelist = namelist,
                                code = Cons(19, code),
                            ),
                        )

                    expression.car.eq(LEQ) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = comp(
                                expression = expression.caddr,
                                namelist = namelist,
                                code = Cons(20, code),
                            ),
                        )

                    expression.car.eq(CAR) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = Cons(10, code),
                        )

                    expression.car.eq(CDR) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = Cons(11, code),
                        )

                    expression.car.eq(ATOM) ->
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = Cons(12, code),
                        )

                    expression.car.eq(CONS) ->
                        comp(
                            expression = expression.caddr,
                            namelist = namelist,
                            code = comp(
                                expression = expression.cadr,
                                namelist = namelist,
                                code = Cons(13, code),
                            ),
                        )

                    expression.car.eq(IF) -> {
                        val thenExp = comp(
                            expression = expression.caddr,
                            namelist = namelist,
                            code = Cons(9, SymAtom.NIL),
                        )
                        val elseExp = comp(
                            expression = expression.cadddr,
                            namelist = namelist,
                            code = Cons(9, SymAtom.NIL),
                        )
                        comp(
                            expression = expression.cadr,
                            namelist = namelist,
                            code = Cons(8, Cons(thenExp, Cons(elseExp, code)))
                        )
                    }

                    expression.car.eq(LAMBDA) -> {
                        val body = comp(
                            expression = expression.caddr,
                            namelist = Cons(expression.cadr, namelist),
                            code = Cons(5, SymAtom.NIL),
                        )
                        Cons(3, Cons(body, code))
                    }

                    expression.car.eq(LET) -> {
                        val m = Cons(vars(expression.cddr), namelist)
                        val args = exprs(expression.cddr)
                        val body = comp(expression.cadr, m, Cons(5, SymAtom.NIL))
                        complis(
                            expression = args,
                            namelist = namelist,
                            code = Cons(3, Cons(body, Cons(4, code))),
                        )
                    }

                    expression.car.eq(LETREC) -> {
                        val m = Cons(vars(expression.cddr), namelist)
                        val args = exprs(expression.cddr)
                        val body = comp(expression.cadr, m, Cons(5, SymAtom.NIL))
                        val res = complis(
                            expression = args,
                            namelist = m,
                            code = Cons(3, Cons(body, Cons(7, code))),
                        )
                        Cons(6, res)
                    }

                    else -> complis(
                        expression = expression.cdr,
                        namelist = namelist,
                        code = comp(expression.car, namelist, Cons(4, code)),
                    )
                }
            } catch (error: SExpException) {
                lispSynError(0, expression, SymAtom.NIL) // unknown error
                return code
            }
        }

        /**
         * Returns SECD machine code compiled from the list of LispKit LISP
         * expressions `e', of the form (e1 e2 ... ek), with `c' appended
         * to it. The resulting code is of the form
         *   (LDC NIL)|ek*n|(CONS)| ... |e1*n|(CONS)|c
         */
        @Throws(SExpException::class)
        private fun complis(expression: SExp, namelist: SExp, code: SExp): SExp {
            return if (expression == SymAtom.NIL) {
                Cons(2, Cons(SymAtom.NIL, code))
            } else {
                complis(expression.cdr, namelist, comp(expression.car, namelist, Cons(13, code)))
            }
        }

        /**
         * Retreives the list of variable names from the list of
         *        (name.LispKit LISP expression) Cons pairs.
         */
        @Throws(SExpException::class)
        private fun vars(dump: SExp): SExp {
            return if (dump == SymAtom.NIL) {
                dump
            } else {
                val variable = dump.caar
                val list = vars(dump.cdr)
                Cons(variable, list)
            }
        }

        /**
         * Retreives the list of LispKit LISP expressions from the list of
         *        (name.LispKit LISP expression) Cons pairs.
         */
        @Throws(SExpException::class)
        private fun exprs(dump: SExp): SExp {
            return if (dump == SymAtom.NIL) {
                dump
            } else {
                Cons(dump.cdar, exprs(dump.cdr))
            }
        }

        /**
         * Returns the location of name `x' in namelist `n'.
         * Location is a Cons of the form (p.q), where p is the position
         * in `n' of the sublist `x' is in, and q is the position
         * of `x' in that sublist.
         */
        @Throws(SExpException::class)
        private fun location(x: SExp, namelist: SExp): SExp {
            return if (member(x, namelist.car)) {
                Cons(0, position(x, namelist.car))
            } else {
                val location = location(x, namelist.cdr)
                Cons(location.car.intValue + 1, location.cdr)
            }
        }

        /**
         * Returns the position of name `x' in list `a'.
         */
        @Throws(SExpException::class)
        private fun position(x: SExp, a: SExp): Int {
            return if (x == a.car) 0 else 1 + position(x, a.cdr)
        }

        /**
         * Returns "true" if `x' is a member of list `a' otherwise "false".
         */
        private fun member(x: SExp, a: SExp): Boolean {
            return try {
                if (a == SymAtom.NIL) {
                    false
                } else if (x == a.car) {
                    true
                } else {
                    member(x, a.cdr)
                }
            } catch (error: SExpException) {
                false
            }
        }

        /**
         * Signals a LISP syntax error.
         */
        private fun lispSynError(n: Int, e: SExp, num: SExp) {
            errorCount++
            println("$n : $e : $num")
        }
    }

    private const val QUOTE = "QUOTE"
    private const val EQ = "EQ"
    private const val ADD = "ADD"
    private const val SUB = "SUB"
    private const val MUL = "MUL"
    private const val DIV = "DIV"
    private const val REM = "REM"
    private const val LEQ = "LEQ"
    private const val CAR = "CAR"
    private const val CDR = "CDR"
    private const val ATOM = "ATOM"
    private const val CONS = "CONS"
    private const val IF = "IF"
    private const val LAMBDA = "LAMBDA"
    private const val LET = "LET"
    private const val LETREC = "LETREC"
}
