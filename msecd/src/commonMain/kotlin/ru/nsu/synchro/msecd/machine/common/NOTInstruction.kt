package ru.nsu.synchro.msecd.machine.common

import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.IntAtom
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object NOTInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State) {
        state.stack = Cons(
            when (val value = state.stack.car) {
                is IntAtom -> SymAtom.T
                is SymAtom -> if (value == SymAtom.NIL) SymAtom.T else SymAtom.NIL
                is Cons -> SymAtom.NIL
            },
            state.stack.cdr
        )
        state.code = state.code.cdr
    }
}
