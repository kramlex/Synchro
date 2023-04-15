package ru.nsu.synchro.ast

typealias Instruction = Command

sealed interface Command: AstNode {

    data class SymbolCommand(
        val symbol: String
    ) : Command

    data object START : Command
    data object STOP : Command
    data object STEP : Command
    data object UP : Command
    data object DOWN : Command
    data object RIGHT : Command
    data object LEFT : Command
    data object PASS : Command

    /**
     * Вместе
     *
     * [ ВМЕСТЕ (Действие)] или [ ВМЕСТЕ ([Действие1;Действие2]) ]
     *
     * выполняются в любом порядке
     */
    data class Together(val actions: List<Action>) : Command

    /**
     * Очередь
     *
     * ( ОЧЕРЕДЬ ( Действие )) или ( ОЧЕРЕДЬ ( [Действие1;Действие2;...]  ))
     *
     */
    data class Queue(val actions: List<Action>) : Command
}

