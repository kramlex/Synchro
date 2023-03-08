package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object LEQInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        if (state.stack.cadr.intValue <= state.stack.car.intValue) {
            state.stack = Cons(SymAtom.T, state.stack.cddr)
        } else {
            state.stack = Cons(SymAtom.F, state.stack.cddr)
        }
        state.code = state.code.cdr
    }
}
