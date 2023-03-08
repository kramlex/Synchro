package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException

data object CDRInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.stack = Cons(state.stack.cdar, state.stack.cdr)
        state.code = state.code.cdr
    }
}
