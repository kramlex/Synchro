package ru.nsu.synchro.multi

import MultiLexer
import MultiParser
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.nsu.synchro.ast.Program
import ru.nsu.synchro.multi.utils.prettyPrint

class Parser {
    private val visitor: VisitorImpl by lazy { VisitorImpl() }

    /**
     * /Users/mdubkov/Desktop/Synchro/multi-java/src/main/resources/test-program.synchro
     */
    fun parseFromFile(fileName: String): Program = parse(CharStreams.fromFileName(fileName))

    fun parseFromString(string: String): Program = parse(CharStreams.fromString(string))

    private fun parse(stream: CharStream): Program {
        val lexer = MultiLexer(stream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = MultiParser(tokenStream)

        val programContext: MultiParser.ProgContext = parser.prog()
        val node = (visitor.visitProg(programContext) as? Program) ?: error("incorrect program")
        println(node.prettyPrint())
        return node
    }
}
