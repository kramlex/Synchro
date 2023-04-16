package ru.nsu.synchro.app.machine.runtime

data class Runtime(
    val environment: ProgramEnvironment,
    val debugger: Debugger
)
