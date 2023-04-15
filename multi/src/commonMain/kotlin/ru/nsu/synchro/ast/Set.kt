package ru.nsu.synchro.ast

/**
 * Набор
 *
 * [Имя = ] ([Поток]) или [Имя = ] ([Поток1,Поток1])
 *
 * выполняется в любом порядке
 */
data class Set(val name: Name?, val flows: List<Flow>) : AstNode
