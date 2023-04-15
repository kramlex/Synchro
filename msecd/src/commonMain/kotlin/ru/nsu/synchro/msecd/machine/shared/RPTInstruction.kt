package ru.nsu.synchro.msecd.machine.shared

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object RPTInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {

    }
}
