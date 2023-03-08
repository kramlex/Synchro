import org.junit.Test
import ru.nsu.synchro.msecd.Interpreter
import ru.nsu.synchro.msecd.LispParser
import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.SExp
import java.io.InputStream
import java.util.Scanner
import kotlin.test.assertEquals
import ru.nsu.synchro.msecd.LispCompiler
import ru.nsu.synchro.msecd.sexp.SymAtom.Companion.NIL
import ru.nsu.synchro.msecd.sexp.SExpException
import kotlin.test.assertTrue

class LTest {

    private fun parseFromFile(inFileName: String?): SExp {
        val inputStream: InputStream = javaClass.classLoader.getResourceAsStream(inFileName)!!
        val inputStreamString: String = Scanner(inputStream, "UTF-8").useDelimiter(/* pattern = */ "\\A").next()
        return LispParser.parse(inputStreamString)
    }

    private fun parse(input: String): SExp {
        return LispParser.parse(input)
    }

    @Test
    fun bootstrapTheCompilerShouldWork() {
        val compilerSourceCode: SExp = parseFromFile("compile.lkl")
        val bootstrappedCompiler: SExp = LispCompiler.compile(compilerSourceCode)
        println(compilerSourceCode)
        println(bootstrappedCompiler)
        val verifiedCompiler: SExp = Interpreter.exec(bootstrappedCompiler, Cons(compilerSourceCode, NIL))
        assertEquals(verifiedCompiler.toString(), bootstrappedCompiler.toString())
    }

    /**
     * Compiles the Compiler from source and returns the emitted SECD code.
     * @return the compiled compiler.
     */
    private fun bootsTrapCompiler(): SExp {
        val compilerSourceCode: SExp = parseFromFile("compile.lkl")
        println(compilerSourceCode)
        return LispCompiler.compile(compilerSourceCode)
    }

    @Test
    fun bootstrapMultipleTimesShouldWork() {
        var initialCompiler: SExp = bootsTrapCompiler()
        val compilerSourceCode: SExp = Cons(parseFromFile("compile.lkl"), NIL)
        val start = System.currentTimeMillis()
        for (i in 0..999) {
            val bootstrappedCompiler: SExp = Interpreter.exec(initialCompiler, compilerSourceCode)
            assertEquals(initialCompiler.toString(), bootstrappedCompiler.toString())
            initialCompiler = bootstrappedCompiler
        }
        println("duration: " + (System.currentTimeMillis() - start))
    }

    @Test
    @Throws(SExpException::class)
    fun compileAndRunFactorialShouldWork() {
        val compiler: SExp = bootsTrapCompiler()
        val sourceFactorial: SExp = Cons(parseFromFile("fac.lkl"), NIL)
        val codeFactorial: SExp = Interpreter.exec(compiler, sourceFactorial)
        val result: SExp = Interpreter.exec(codeFactorial, parse("(10)"))
        assertTrue(result.eq(parse("3628800")))
    }

    @Test
    @Throws(SExpException::class)
    fun compileAndRunTakeShouldWork() {
        val compiler: SExp = bootsTrapCompiler()
        val source: SExp = Cons(parseFromFile("take.lkl"), NIL)
        val code: SExp = Interpreter.exec(compiler, source)
        val result: SExp = Interpreter.exec(code, parse("(18 12 6)"))
        assertTrue(result.eq(parse("7")))
    }

    @Test
    @Throws(SExpException::class)
    fun compileAndRunFibShouldWork() {
        val compiler: SExp = bootsTrapCompiler()
        val source: SExp = Cons(parseFromFile("fib.lkl"), NIL)
        val code: SExp = Interpreter.exec(compiler, source)
        val result: SExp = Interpreter.exec(code, parse("(10)"))
        assertTrue(result.eq(parse("89")))
    }
}