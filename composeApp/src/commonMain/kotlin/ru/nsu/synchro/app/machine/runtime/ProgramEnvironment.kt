package ru.nsu.synchro.app.machine.runtime

import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode

interface ProgramEnvironment {
    suspend fun provideEnvVariable(node: EnvNode): Any?
    suspend fun callForeignFunction(node: ForeignFunctionNode): Any?
}
