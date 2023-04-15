package ru.nsu.synchro.msecd.machine.common

import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.machine.State

data object STOPInstruction : Instruction {
    override fun execute(state: State) {
        //NOOP
    }
}
