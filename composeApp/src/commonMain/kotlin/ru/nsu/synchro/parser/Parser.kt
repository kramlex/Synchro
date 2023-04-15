package ru.nsu.synchro.parser

import ru.nsu.synchro.ast.Program

expect class Parser {
    fun parse(string: String): Program
}