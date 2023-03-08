package ru.nsu.synchro.msecd.machine

import ru.nsu.synchro.msecd.machine.shared.SharedMemory
import ru.nsu.synchro.msecd.sexp.SExp

class State(
    var sharedMemory: SharedMemory,
    var stack: SExp,
    var env: SExp,
    var code: SExp,
    var dump: SExp
)
