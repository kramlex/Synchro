package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State

data object STOPInstruction : Instruction {
    override fun execute(state: State) {
        //NOOP
    }
}
