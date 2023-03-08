package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object SELInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.dump = Cons(state.code.cdddr, state.dump)
        if (state.stack.car.eq(SymAtom.T)) {
            state.code = state.code.cadr
        } else {
            state.code = state.code.caddr
        }
        state.stack = state.stack.cdr
    }
}
