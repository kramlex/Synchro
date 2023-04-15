package ru.nsu.synchro.msecd.machine.shared

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object SETInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        val cadr = state.code.cadr
        val sym: SymAtom = (cadr as? SymAtom)
            ?.takeIf { !SymAtom.reserved.contains(it) }
            ?: throw SExpException("incorrect variable name with expression $cadr")
        state.sharedMemory.setMemory(key = sym, value = state.stack.car, stateName = state.name)
        state.stack = state.stack.cdr
        state.code = state.code.cddr
    }
}
