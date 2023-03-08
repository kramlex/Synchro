package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException

data object RTNInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.stack = Cons(state.stack.car, state.dump.car)
        state.env = state.dump.cadr
        state.code = state.dump.caddr
        state.dump = state.dump.cdddr
    }
}
