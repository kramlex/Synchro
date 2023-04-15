package ru.nsu.synchro.parser

import ru.nsu.synchro.ast.Context
import ru.nsu.synchro.ast.Enumeration
import ru.nsu.synchro.ast.Number
import ru.nsu.synchro.ast.Text
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    val prog = """
        ((hello = 5.5); (world=2); (k="dfdf")) (
    """.trimIndent()

    @Test
    fun justRunWithoutTest() {
        val tokens = prog.multiTokenize()
        tokens.forEach { println(it) }
    }

    @Test
    fun justRunWithoutTest2() {
        val tokens = "5!5545".multiTokenize()
        tokens.forEach { println(it) }
    }

    @Test
    fun parseDataTest() {
        val parser = MultiParser()
        val program = "(((1,2),2),2,3)"
        val parseResult = parser.parseData(program.multiTokenize())

        assertEquals(
            expected = Enumeration(
                Enumeration(Enumeration(Number(1.0), Number(2.0)),Number(2.0)),
                Number(2.0),
                Number(3.0)
            ),
            actual = parseResult.first
        )
    }

    @Test
    fun parseContextTest() {
        val parser = MultiParser()
        val program = "((hello = 5); (world=2); (k=\"dfdf\")) 1 2 3"
        val parseResult = parser.parseContext(program.multiTokenize())

        assertEquals(
            actual = parseResult.first,
            expected = Context(mapOf(
                "hello" to Number(5.0), "world" to Number(2.0), "k" to Text("dfdf")
            ))
        )
    }
}