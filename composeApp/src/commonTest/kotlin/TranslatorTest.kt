
import kotlinx.coroutines.runBlocking
import ru.nsu.synchro.app.gui.gameV2.PhilosophersParallelGame
import ru.nsu.synchro.app.gui.gameV2.PhilosophersParallelGameTranslator
import ru.nsu.synchro.app.parser.Parser
import ru.nsu.synchro.utils.prettyPrint
import kotlin.test.Test

class TranslatorTest {

    @Test
    fun translatorTest() {
        val parser = Parser()
        val program = parser.parseFromFile("/Users/mdubkov/Desktop/Synchro/multi-java/src/main/resources/test-program.synchro")
        val parallelProgram = PhilosophersParallelGameTranslator.translateProgram(program)
        println(parallelProgram.prettyPrint())
    }

    @Test
    fun executeProgramFromTranslator() = runBlocking {
        val parser = Parser()
        val program = parser.parseFromFile("/Users/mdubkov/Desktop/Synchro/multi-java/src/main/resources/test-program.synchro")
        val game = PhilosophersParallelGame(program, debug = true)
        game.runProgram()
    }
}