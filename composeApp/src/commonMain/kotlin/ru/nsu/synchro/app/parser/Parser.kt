package ru.nsu.synchro.app.parser

import ru.nsu.synchro.ast.Program

expect class Parser() {
    fun parse(string: String): Program
    fun parseFromFile(filePath: String): Program
}