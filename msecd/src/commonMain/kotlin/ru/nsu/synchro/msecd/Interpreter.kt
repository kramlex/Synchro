package ru.nsu.synchro.msecd

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.machine.shared.SharedMemory
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExp
import ru.nsu.synchro.msecd.sexp.SymAtom

internal object Interpreter {
    private const val STOP = 21

    /**
     * Executes the (M)SECD program with the given arguments.
     */
    fun exec(code: SExp, argList: SExp): SExp {
        val sharedMemory = SharedMemory()
        val state = State(
            name = "main",
            sharedMemory = sharedMemory,
            stack = Cons(argList, SymAtom.NIL),
            env = SymAtom.NIL,
            code = code,
            dump = SymAtom.NIL
        )
        var instructionIndex: Int = state.code.car.intValue
        while (instructionIndex != STOP) {
            ShortInstruction.get(instructionIndex)?.execute(state)
            instructionIndex = state.code.car.intValue
        }
        return state.stack.car
    }
}
