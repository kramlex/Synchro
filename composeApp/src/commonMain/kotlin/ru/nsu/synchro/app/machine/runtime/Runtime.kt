package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.flow.MutableStateFlow

data class Runtime(
    val name: String,
    val environment: ProgramEnvironment,
    val debugger: Debugger,
    val isRunning: MutableStateFlow<Boolean>
)
