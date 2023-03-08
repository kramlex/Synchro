/*
 * EQInstruction.java
 *
 * Title:  An Implementation of the Programming Language LispKit LISP in Java
 * Author: Milos Radovanovic, University of Novi Sad
 * Date:   February 2002
 *
 */
package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object EQInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        if (state.stack.car.eq(state.stack.cadr)) {
            state.stack = Cons(SymAtom.T, state.stack.cddr)
        } else {
            state.stack = Cons(SymAtom.F, state.stack.cddr)
        }
        state.code = state.code.cdr
    }
}
