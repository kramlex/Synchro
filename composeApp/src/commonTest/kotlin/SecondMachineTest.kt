
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import ru.nsu.synchro.app.machine.ast.EnvNode
import ru.nsu.synchro.app.machine.ast.ForeignFunctionNode
import ru.nsu.synchro.app.machine.dsl.program
import ru.nsu.synchro.app.machine.runtime.ProgramEnvironment
import ru.nsu.synchro.app.machine.runtime.executeProgram
import kotlin.native.concurrent.ThreadLocal
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal class Phil(
    val maxPosition: Int = 3
) {
    val currentPosition = MutableStateFlow(0)

    val leftForkTaken = MutableStateFlow(false)
    val rightForkTaken = MutableStateFlow(false)
}

@ThreadLocal
object `Среда философов` : ProgramEnvironment {

    private val phils: List<Phil> = listOf(
        Phil(3),
        Phil(3)
    )

    private val semaphore1 = Semaphore(1, 0)
    private val semaphore2 = Semaphore(1, 0)

    override suspend fun provideEnvVariable(node: EnvNode): Any? = when (node.name) {
        "Впереди свободно 1" -> phils[0].currentPosition.value < phils[1].maxPosition
        "Сзади свободно 1" -> phils[0].currentPosition.value > 0
        "Впереди свободно 2" -> phils[1].currentPosition.value < phils[1].maxPosition
        "Сзади свободно 2" -> phils[1].currentPosition.value > 0
        else -> null
    }

    override suspend fun callForeignFunction(node: ForeignFunctionNode) {
        when (node.name) {
            "Шаг вперёд 1" -> phils[0].currentPosition.update { it.inc() }
            "Шаг назад 1" -> phils[0].currentPosition.update { it.dec() }
            "Шаг вперёд 2" -> phils[1].currentPosition.update { it.inc() }
            "Шаг назад 2" -> phils[1].currentPosition.update { it.dec() }

            "Взять левую вилку 1" -> with(semaphore1) {
                acquire()
                phils[0].leftForkTaken.value = true
            }

            "Взять правую вилку 1" -> with(semaphore2) {
                acquire()
                phils[0].rightForkTaken.value = true
            }

            "Взять левую вилку 2" -> with(semaphore2) {
                acquire()
                phils[1].leftForkTaken.value = true
            }

            "Взять правую вилку 2" -> with(semaphore1) {
                acquire()
                phils[1].rightForkTaken.value = true
            }

            "Еда 1" -> println("1 Начал есть!!")
            "Еда 2" -> println("2 Начал есть!!")

            "Положить левую вилку 1" ->  {
                phils[0].leftForkTaken.value = false
                semaphore1.release()
            }
            "Положить правую вилку 1" -> {
                phils[0].rightForkTaken.value = false
                semaphore2.release()
            }

            "Положить левую вилку 2" -> {
                phils[1].leftForkTaken.value = false
                semaphore2.release()
            }
            "Положить правую вилку 2" -> {
                phils[1].leftForkTaken.value = false
                semaphore1.release()
            }
        }
    }
}

private val program = program(name = "Столовая") {
    "Философ1".synchronous {
        "Идти к столу 1".synchronous {
            whileLoop(envName = "Впереди свободно 1") {
                "Шаг вперёд 1"()
            }
        }
        "У стола 1".synchronous {
            parallel {
                "Взять левую вилку 1"()
                "Взять правую вилку 1"()
            }
            "Еда 1"()
            delay(10.seconds)
        }

        parallel {
            "Положить левую вилку 1"()
            "Положить правую вилку 1"()
        }
        "Обратно 1".synchronous {
            whileLoop(envName = "Сзади свободно 1") {
                "Шаг назад 1"()
            }
        }
    }

    "Философ2".synchronous {
        delay(4.seconds)
        "Идти к столу 2".synchronous {
            whileLoop(envName = "Впереди свободно 2") {
                "Шаг вперёд 2"()
            }
        }
        "У стола 2".synchronous {
            parallel {
                "Взять левую вилку 2"()
                "Взять правую вилку 2"()
            }
            "Еда 2"()
            delay(10.seconds)
        }

        parallel {
            "Положить левую вилку 2"()
            "Положить правую вилку 2"()
        }
        "Обратно 2".synchronous {
            whileLoop(envName = "Сзади свободно 2") {
                "Шаг назад 2"()
            }
        }
    }
}.returns(
    string = "Оба поели"
)

class SecondMachineTest {

    @Test
    fun twoPhilsTest() = runBlocking {
        executeProgram(
            `Среда философов`,
            program,
            debug = true
        )
    }
}