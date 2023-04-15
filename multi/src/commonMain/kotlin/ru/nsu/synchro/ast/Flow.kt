package ru.nsu.synchro.ast

/**
 * Поток
 *
 * [Имя = ]  ( Действие ) или [Имя = ] ( [Действие1; Действие2; ...] )
 *
 * выполняется по очереди
 */
data class Flow(val name: Name?, val actions: List<Action>): AstNode
