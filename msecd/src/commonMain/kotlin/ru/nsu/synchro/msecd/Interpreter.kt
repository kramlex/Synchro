package ru.nsu.synchro.msecd

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.machine.instructions.*
import ru.nsu.synchro.msecd.machine.shared.SharedMemory
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExp
import ru.nsu.synchro.msecd.sexp.SymAtom

internal object Interpreter {
    private const val STOP = 21

    /**
     * the (M)SECD instruction set.
    */
    val instructions: List<Instruction?> = listOf(
        null, // 0
        LDInstruction, // 1
        LDCInstruction, // 2
        LDFInstruction, // 3
        APInstruction, // 4
        RTNInstruction, // 5
        DUMInstruction, // 6
        RAPInstruction, // 7
        SELInstruction, // 8
        JOINInstruction, // 9
        CARInstruction, // 10
        CDRInstruction, // 11
        ATOMInstruction, // 12
        CONSInstruction, // 13
        EQInstruction, // 14
        ADDInstruction, // 15
        SUBInstruction, // 16
        MULInstruction, // 17
        DIVInstruction, // 18
        REMInstruction, // 19
        LEQInstruction, // 20
        STOPInstruction // 21

        // TODO: add additional (M)SECD commands
    )

    /**
     * Executes the (M)SECD program with the given arguments.
     */
    fun exec(code: SExp, argList: SExp): SExp {
        val sharedMemory = SharedMemory(SymAtom.NIL)
        val state = State(
            sharedMemory = sharedMemory,
            stack = Cons(argList, SymAtom.NIL),
            env = SymAtom.NIL,
            code = code,
            dump = SymAtom.NIL
        )
        var instructionIndex: Int = state.code.car.intValue
        while (instructionIndex != STOP) {
            instructions[instructionIndex]?.execute(state)
            instructionIndex = state.code.car.intValue
        }
        return state.stack.car
    }
}
