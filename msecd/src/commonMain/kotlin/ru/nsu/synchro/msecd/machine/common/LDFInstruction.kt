package ru.nsu.synchro.msecd.machine.common

import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException

data object LDFInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.stack = Cons(Cons(state.code.cadr, state.env), state.stack)
        state.code = state.code.cddr
    }
}
