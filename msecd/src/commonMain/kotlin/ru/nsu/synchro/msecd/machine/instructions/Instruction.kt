package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.SExpException

interface Instruction {
    @Throws(SExpException::class)
    fun execute(state: State)
}
