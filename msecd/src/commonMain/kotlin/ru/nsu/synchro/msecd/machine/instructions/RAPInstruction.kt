package ru.nsu.synchro.msecd.machine.instructions

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExpException
import ru.nsu.synchro.msecd.sexp.SymAtom

data object RAPInstruction : Instruction {
    @Throws(SExpException::class)
    override fun execute(state: State)  {
        state.dump = Cons(
            state.stack.cddr,
            Cons(
                state.env.cdr,
                Cons(state.code.cdr, state.dump)
            )
        )
        state.env = state.stack.cdar
        state.env = state.env.rplaca(state.stack.cadr)
        state.code = state.stack.caar
        state.stack = SymAtom.NIL
    }
}
