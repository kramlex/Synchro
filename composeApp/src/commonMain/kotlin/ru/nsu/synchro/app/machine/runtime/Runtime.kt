package ru.nsu.synchro.app.machine.runtime

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

data class Runtime(
    val environment: ProgramEnvironment,
    val debugger: Debugger
) {
    val scope = CoroutineScope(Dispatchers.Default)
}
