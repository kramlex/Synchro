package ru.nsu.synchro.ast

import kotlin.Number

sealed interface Data : AstNode

data class Number(val number: Number) : Data
data class Text(val text: String) : Data
data class QuotedString(val string: String) : Data
data class Cell(val position: Position) : Data
data class Enumeration(val elements: List<Data>) : Data {
    constructor(vararg data: Data) : this(elements = data.toList())
}
data class Size(val x: Int, val y: Int) : Data
data class Position(val x: Int, val y: Int)
