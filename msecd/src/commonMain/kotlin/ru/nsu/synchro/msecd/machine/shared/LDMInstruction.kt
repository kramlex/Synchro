package ru.nsu.synchro.msecd.machine.shared

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object LDMInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        val cadr = state.code.cadr
        val sym: SymAtom = (cadr as? SymAtom)
            ?.takeIf { !SymAtom.reserved.contains(it) }
            ?: throw SExpException("incorrect variable name with expression $cadr")
        val value = state.sharedMemory.getMemory(sym)
        state.stack = Cons(value, state.stack)
        state.code = state.code.cddr
    }
}
