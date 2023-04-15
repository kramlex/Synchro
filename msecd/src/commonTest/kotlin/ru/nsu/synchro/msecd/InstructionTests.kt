package ru.nsu.synchro.msecd

import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.machine.shared.SharedMemory
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SymAtom
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionTests {

    private fun execute(state: State) {
        while (state.code is Cons && state.code.car != SymAtom.NIL) {
            val instruction: Instruction? = ShortInstruction.get(state.code.car.intValue)
            instruction?.execute(state)
        }
    }

    @Test
    fun test_LDC() {
        val sharedMemory = SharedMemory()
        val state = State(
            name = "testADD",
            sharedMemory = sharedMemory,
            stack = SymAtom.NIL,
            env = SymAtom.NIL,
            code = Cons(
                ldc.atom,
                Cons(
                    2,
                    SymAtom.NIL
                )
            ),
            dump = SymAtom.NIL
        )

        execute(state)

        assertEquals(
            expected = Cons(2),
            actual = state.stack
        )
    }
}