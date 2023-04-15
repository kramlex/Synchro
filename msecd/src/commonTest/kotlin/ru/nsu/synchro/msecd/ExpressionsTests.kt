package ru.nsu.synchro.msecd

import ru.nsu.synchro.msecd.machine.State
import ru.nsu.synchro.msecd.machine.common.ADDInstruction
import ru.nsu.synchro.msecd.machine.common.APInstruction
import ru.nsu.synchro.msecd.machine.common.ATOMInstruction
import ru.nsu.synchro.msecd.machine.common.CARInstruction
import ru.nsu.synchro.msecd.machine.common.CDRInstruction
import ru.nsu.synchro.msecd.machine.common.CONSInstruction
import ru.nsu.synchro.msecd.machine.common.DIVInstruction
import ru.nsu.synchro.msecd.machine.common.DUMInstruction
import ru.nsu.synchro.msecd.machine.common.EQInstruction
import ru.nsu.synchro.msecd.machine.Instruction
import ru.nsu.synchro.msecd.machine.common.JOINInstruction
import ru.nsu.synchro.msecd.machine.common.LDCInstruction
import ru.nsu.synchro.msecd.machine.common.LDFInstruction
import ru.nsu.synchro.msecd.machine.common.LDInstruction
import ru.nsu.synchro.msecd.machine.common.LEQInstruction
import ru.nsu.synchro.msecd.machine.common.MULInstruction
import ru.nsu.synchro.msecd.machine.common.NOTInstruction
import ru.nsu.synchro.msecd.machine.common.RAPInstruction
import ru.nsu.synchro.msecd.machine.common.REMInstruction
import ru.nsu.synchro.msecd.machine.common.RTNInstruction
import ru.nsu.synchro.msecd.machine.common.SELInstruction
import ru.nsu.synchro.msecd.machine.common.STOPInstruction
import ru.nsu.synchro.msecd.machine.common.SUBInstruction
import ru.nsu.synchro.msecd.machine.shared.LDMInstruction
import ru.nsu.synchro.msecd.machine.shared.SETInstruction
import ru.nsu.synchro.msecd.machine.shared.SharedMemory
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExp
import ru.nsu.synchro.msecd.sexp.SymAtom
import kotlin.test.Test

class ExpressionsTests {

    private fun parse(string: String): SExp = ExpressionParser.parse(string)

    @Test
    fun test() {
        val exp = Cons(Cons(parse("10"), parse("20")), Cons(parse("30"), parse("40")))
        println(exp)
        println(exp.cddr)
    }
}