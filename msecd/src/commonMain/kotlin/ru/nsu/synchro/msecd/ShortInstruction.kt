package ru.nsu.synchro.msecd

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
import ru.nsu.synchro.msecd.sexp.IntAtom

/**
 * the (M)SECD instruction set.
 */
enum class ShortInstruction(val instruction: Instruction?) {
    NULL(null),   // 0
    LD(LDInstruction),     // 1
    LDC(LDCInstruction),    // 2
    LDF(LDFInstruction),    // 3
    AP(APInstruction),     // 4
    RTN(RTNInstruction),    // 5
    DUM(DUMInstruction),    // 6
    RAP(RAPInstruction),    // 7
    SEL(SELInstruction),    // 8
    JOIN(JOINInstruction),   // 9
    CAR(CARInstruction),    // 10
    CDR(CDRInstruction),    // 11
    ATOM(ATOMInstruction),   // 12
    CONS(CONSInstruction),   // 13
    EQ(EQInstruction),     // 14
    ADD(ADDInstruction),    // 15
    SUB(SUBInstruction),    // 16
    MUL(MULInstruction),    // 17
    DIV(DIVInstruction),    // 18
    REM(REMInstruction),    // 19
    LEQ(LEQInstruction),    // 20
    STOP(STOPInstruction),   // 21
    NOT(NOTInstruction),    // 22
    LDM(LDMInstruction),    // 23
    SET(SETInstruction);    // 24

    val index: Int get() = values().indexOf(this)

    val atom: IntAtom get() = IntAtom(index)

    companion object {
        fun get(index: Int): Instruction? = ShortInstruction.values()[index].instruction
    }
}

val nil: ShortInstruction = ShortInstruction.NULL
val ld: ShortInstruction = ShortInstruction.LD
val ldc: ShortInstruction = ShortInstruction.LDC
val ldf: ShortInstruction = ShortInstruction.LDF
val ap: ShortInstruction = ShortInstruction.AP
val rtn: ShortInstruction = ShortInstruction.RTN
val dum: ShortInstruction = ShortInstruction.DUM
val rap: ShortInstruction = ShortInstruction.RAP
val sel: ShortInstruction = ShortInstruction.SEL
val join: ShortInstruction = ShortInstruction.JOIN
val car: ShortInstruction = ShortInstruction.CAR
val cdr: ShortInstruction = ShortInstruction.CDR
val atom: ShortInstruction = ShortInstruction.ATOM
val cons: ShortInstruction = ShortInstruction.CONS
val eq: ShortInstruction = ShortInstruction.EQ
val add: ShortInstruction = ShortInstruction.ADD
val sub: ShortInstruction = ShortInstruction.SUB
val mul: ShortInstruction = ShortInstruction.MUL
val div: ShortInstruction = ShortInstruction.DIV
val rem: ShortInstruction = ShortInstruction.REM
val leq: ShortInstruction = ShortInstruction.LEQ
val stop: ShortInstruction = ShortInstruction.STOP
val not: ShortInstruction = ShortInstruction.NOT
val ldm: ShortInstruction = ShortInstruction.LDM
val set: ShortInstruction = ShortInstruction.SET
