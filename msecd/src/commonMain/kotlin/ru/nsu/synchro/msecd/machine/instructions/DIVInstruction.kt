package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException

data object DIVInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.stack = Cons(state.stack.cadr.intValue / state.stack.car.intValue, state.stack.cddr)
        state.code = state.code.cdr
    }
}
