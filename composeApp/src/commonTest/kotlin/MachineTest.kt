
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode
import ru.nsu.synchro.app.machine.dsl.program
import ru.nsu.synchro.app.machine.runtime.ProgramEnvironment
import ru.nsu.synchro.app.machine.runtime.executeProgram
import kotlin.native.concurrent.ThreadLocal
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

@ThreadLocal
object `Среда философа` : ProgramEnvironment {
    private var currentPosition1 = 0

    private var fork1Taken = MutableStateFlow(false)
    private val fork1Mutex = Mutex()
    private var fork2Taken = MutableStateFlow(false)
    private val fork2Mutex = Mutex()

    override suspend fun provideEnvVariable(node: EnvNode): Any? = when (node.name) {
        "Впереди свободно 1" -> currentPosition1 < 3
        "Сзади свободно 1" -> currentPosition1 > 0
        else -> null
    }

    override suspend fun callForeignFunction(node: ForeignFunctionNode) {
        when (node.name) {
            "Шаг вперёд 1" -> currentPosition1++
            "Шаг назад 1" -> currentPosition1--
            "Взять вилку 1" -> fork1Mutex.withLock {
                fork1Taken.first { taken -> !taken }
                fork1Taken.value = true
            }
            "Взять вилку 2" -> fork2Mutex.withLock {
                fork2Taken.first { taken -> !taken }
                fork2Taken.value = true
            }
            "Еда 1" -> println("Поел!!")
            "Положить вилку 1" -> {
                fork1Taken.value = false
            }
            "Положить вилку 2" -> {
                fork2Taken.value = false
            }
        }
    }
}

private val program = program(name = "Столовая") {
    "Философ".synchronous {
        "Начал есть 1".synchronous {
            whileLoop(envName = "Впереди свободно 1") {
                "Шаг вперёд 1"()
            }
        }
        "У стола 1".synchronous {
            parallel {
                "Взять вилку 1"()
                "Взять вилку 2"()
            }
            "Еда 1"()
        }
        parallel {
            delay(10.seconds)
            "Положить вилку 1"()
            "Положить вилку 2"()
        }
        "Обратно 1".synchronous {
            whileLoop(envName = "Сзади свободно 1") {
                "Шаг назад 1"()
            }
        }
    }
}.returns(
    string = "Оба поели"
)

class MachineTest {

    @Test
    fun machineTest() = runBlocking {
        executeProgram(
            `Среда философа`,
            program,
            debug = true
        )
    }
}