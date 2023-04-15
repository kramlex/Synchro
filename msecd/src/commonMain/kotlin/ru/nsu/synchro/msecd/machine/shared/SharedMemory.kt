package ru.nsu.synchro.msecd.machine.shared

import ru.nsu.synchro.msecd.sexp.SExp
import ru.nsu.synchro.msecd.sexp.SymAtom

class SharedMemory {
    private var memory: HashMap<SymAtom, ArrayDeque<VariableEntry>> = HashMap()

    fun getMemory(symAtom: SymAtom): SExp {
        return memory[symAtom]?.last()?.value
            ?: throw SharedMemoryException("variable ${symAtom.stringValue} has no value")
    }

    fun setMemory(key: SymAtom, value: SExp, stateName: String) {
        val newValue = VariableEntry(value, stateName)
        memory.set(key = key, memory[key]?.apply { addLast(newValue) } ?: ArrayDeque(listOf(newValue)))
    }
}

data class VariableEntry(
    val value: SExp,
    val stateName: String
)
