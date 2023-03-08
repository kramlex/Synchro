package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException

data object CONSInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.stack = Cons(Cons(state.stack.car, state.stack.cadr), state.stack.cddr)
        state.code = state.code.cdr
    }
}
