package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException

data object LDCInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.stack = Cons(state.code.cadr, state.stack)
        state.code = state.code.cddr
    }
}
