
import org.junit.jupiter.api.Test
import ru.nsu.synchro.multi.Parser
import ru.nsu.synchro.utils.prettyPrint

class ParserTest {

    @Test
    fun test() {
        val parser = Parser()
        val program = parser.parseFromFile("/Users/mdubkov/Desktop/Synchro/multi-java/src/main/resources/test-program.synchro")
        println(program.prettyPrint())
    }
}