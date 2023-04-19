package ru.nsu.synchro.code.program

internal sealed interface Program {
    object None : Program

    data class Synchro(val program: ru.nsu.synchro.ast.Program) : Program
}
