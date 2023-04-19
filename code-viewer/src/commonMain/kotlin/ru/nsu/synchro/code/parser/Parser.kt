package ru.nsu.synchro.code.parser

import ru.nsu.synchro.ast.Program

interface Parser {
    fun parse(string: String): Program
    fun parseFromFile(filePath: String): Program
}