package ru.nsu.synchro.parser

import ru.nsu.synchro.ast.Program
import ru.nsu.synchro.multi.Parser

actual class Parser {
    private val parser = Parser()

    actual fun parse(string: String): Program =
        parser.parseFromString(string)
}
