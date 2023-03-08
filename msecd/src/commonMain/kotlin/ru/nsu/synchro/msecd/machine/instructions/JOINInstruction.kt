package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.SExpException

data object JOINInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.code = state.dump.car
        state.dump = state.dump.cdr
    }
}
