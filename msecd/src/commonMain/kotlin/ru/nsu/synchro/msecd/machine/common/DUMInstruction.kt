package ru.nsu.synchro.msecd.machine.common

import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object DUMInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.env = Cons(SymAtom.NIL, state.env)
        state.code = state.code.cdr
    }
}
