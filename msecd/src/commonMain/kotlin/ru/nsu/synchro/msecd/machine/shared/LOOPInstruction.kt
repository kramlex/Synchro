package ru.nsu.synchro.msecd.machine.shared

import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException

data object LOOPInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.dump = Cons(state.code.cdar, state.code.cdr)
        state.code = state.code.cdar
    }
}
