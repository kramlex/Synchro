package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Atom
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object ATOMInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        if (state.stack.car is Atom) {
            state.stack = Cons(SymAtom.T, state.stack.cdr)
        } else {
            state.stack = Cons(SymAtom.F, state.stack.cdr)
        }
        state.code = state.code.cdr
    }
}
