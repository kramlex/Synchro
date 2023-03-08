package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException

data object LDInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        var temp = state.env
        for (i in 1..state.code.caadr.intValue) temp = temp.cdr
        temp = temp.car
        for (i in 1..state.code.cdadr.intValue) temp = temp.cdr
        temp = temp.car
        state.stack = Cons(temp, state.stack)
        state.code = state.code.cddr
    }
}