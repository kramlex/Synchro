package ru.nsu.synchro.parser

import ru.nsu.synchro.ast.Context
import ru.nsu.synchro.ast.Data
import ru.nsu.synchro.ast.Enumeration
import ru.nsu.synchro.ast.Name
import ru.nsu.synchro.ast.Number
import ru.nsu.synchro.ast.Program
import ru.nsu.synchro.ast.Set
import ru.nsu.synchro.ast.Text

private typealias Tokens = ArrayDeque<String>

private const val UNARY_TOKENS = "*()=;,?<>/\\[]"
private const val STRINGS_QUOTES = "\"'`"

internal class MultiParser {
    fun parseProgram(programText: String): Program {
        val tokens = Tokens(programText.multiTokenize())
        while (true) {
            val token = tokens.nextToken()

            when (token) {
                "(" -> {
                    parseContext(tokens)
                }
            }
        }
    }

    /**
     * example: ((hello = 5); (world=2); (k="dfdf"))
     */
    fun parseContext(_tokens: Tokens): Pair<Context?, Tokens> {
        var tokens = _tokens
        tokens.removeFirst()
        val contextData: MutableMap<Name, Data> = mutableMapOf()

        while (true) {
            if (tokens.nextToken() == ")" || tokens.nextToken() != "(")
                return Pair(null, tokens)

            tokens.removeFirst()

            val name = tokens.nextTokenWithDelete()
            val equals = tokens.nextTokenWithDelete()

            val parsedResult = parseData(tokens)
            val data = parsedResult.first

            if (name != null && equals == "=" && data != null) {
                tokens = parsedResult.second
                contextData[name] = data
            } else {
                return Pair(null, tokens)
            }

            if (tokens.nextToken() == ")") {
                tokens.removeFirst()
            } else {
                return Pair(null, tokens)
            }

            if (tokens.nextToken() == ")") {
                break
            }
            if (tokens.nextToken() == ";") {
                tokens.removeFirst()
            }
        }
        tokens.removeFirstOrNull()
        return Pair(Context(contextData), tokens)
    }

    fun parseData(tokens: Tokens): Pair<Data?, Tokens> {
        return when(tokens.nextToken()) {
            // начинается с (
            roundBracketStart -> {
                val tempData: MutableList<Data> = mutableListOf()
                var tempTokens = tokens.copy()
                tempTokens.removeFirst()
                while (true) {
                    val nextToken = tempTokens.nextToken() ?: break

                    if (UNARY_TOKENS.contains(nextToken) &&
                        nextToken != roundBracketEnd &&
                        nextToken != roundBracketStart
                    ) tempTokens.removeFirst()

                    val parsed = parseData(tempTokens.copy())

                    val parsedData = parsed.first ?: break

                    parsedData.also { tempData.add(it) }

                    tempTokens = parsed.second

                    if (parsed.first == null || parsed.second.nextToken() == roundBracketEnd) {
                        tempTokens.removeFirstOrNull()
                        break
                    }
                }
                Pair(Enumeration(elements = tempData.toList()), tempTokens)
            }
            // иначе
            else -> {
                val tempTokens: MutableList<String> = mutableListOf()
                while (true) {
                    val nextToken = tokens.nextToken()
                        ?.takeIf { UNARY_TOKENS.contains(it).not() }
                        ?: break
                    nextToken.also { tempTokens.add(it) }
                    tokens.removeFirst()
                }
                if (tempTokens.any { it.isSurrounded() }) {
                    val newTokens = tempTokens.map { it.removeDoubleQuoteSurrounding() }
                    val result = newTokens.concatenate()
                    Pair(Text(result).takeIf { result.isNotBlank() }, tokens)
                } else {
                    val result = tempTokens.concatenate()
                    val data: Data = result.toDoubleOrNull()?.let { Number(it) }
                        ?: result.toIntOrNull()?.let { Number(it.toDouble()) }
                        ?: Text(result)
                    Pair(data.takeIf { result.isNotBlank() }, tokens)
                }
            }
        }
    }

    private fun parseSet(tokens: Tokens): Pair<Set?, Tokens> {


        return Pair(null, tokens)
    }

    // TOKENS

    private fun Tokens.nextToken(): String? =
        this.firstOrNull()

    private fun Tokens.nextTokenWithDelete(): String? =
        this.removeFirstOrNull()

    private fun Tokens.copy(): Tokens = Tokens(this.toList())


    // STRINGS EXT

    private fun String.isSurrounded(): Boolean = startsWith(doubleQuote) && endsWith(doubleQuote)
    private fun String.removeDoubleQuoteSurrounding() = removeSurrounding(doubleQuote, doubleQuote)
    private fun List<String>.concatenate(): String = joinToString(emptyString)

    companion object {

        const val startKeyWord = "ПУСК"
        const val stopKeyWord = "СТОП"
        const val stepKeyWord = "ШАГ"
        const val upKeyWord = "ВВЕРХ"
        const val downKeyWord = "ВНИЗ"
        const val rightKeyWord = "ВПРАВО"
        const val leftKeyWord = "ВЛЕВО"
        const val passKeyWord = "ПАС"
        const val togetherKeyWord = "ВМЕСТЕ"
        const val queueKeyWord = "ОЧЕРЕДЬ"
        const val returnKeyWord = "ВОЗВРТ"

        const val doubleQuote = "\""
        const val roundBracketStart = "("
        const val roundBracketEnd = ")"
        const val emptyString = ""
    }
}

fun String.multiTokenize(
    skipSpaces: Boolean = true,
    namesSeparators: String = "_",
    unaryTokens: String = UNARY_TOKENS,
    stringsQuotes: String = STRINGS_QUOTES,
) = mutableListOf<String>().apply {
    var startTokenIdx = 0
    for (finishTokenIdx in 1 until length) {
        if (isDiff(startTokenIdx, finishTokenIdx, namesSeparators, unaryTokens, stringsQuotes)) {
            add(substring(startTokenIdx until finishTokenIdx), skipSpaces)
            startTokenIdx = finishTokenIdx
        }
    }
    add(substring(startTokenIdx until length), skipSpaces)
}.toList().let { Tokens(it) }

private fun MutableList<String>.add(token: String, skipSpaces: Boolean) {
    when {
        !skipSpaces || token.isNotBlank() -> add(token)
    }
}

/**
 * Мы переопределяем понятие разницы между двумя символами,
 * на основе которого строка разбивается на лексемы.
 * @param typeIdx first symbol
 * @param otherIdx other symbol
 * @param namesSeparators list of names-allowed symbols. For example lower_snake_case, css-case-var
 * @param unaryTokens symbols always one token only. For example, {{{ -> {, {, {
 * @param stringsQuotes quote types for string literals, "string" 'string' `string`
 * @return is the second index a break character?
 */
private fun String.isDiff(
    typeIdx: Int,
    otherIdx: Int,
    namesSeparators: String,
    unaryTokens: String,
    stringsQuotes: String,
): Boolean {

    // make sure symbols are available
    val type = get(typeIdx)
    val other = get(otherIdx)

    // handle entities names
    if (type.isLetter()) {
        // Solve names with hyphens and underscores
        // Otherwise, we will assume that the name has been entered.
        return (other !in namesSeparators) && !other.isLetterOrDigit()
    }

    // handle characters that break anyway
    if (type in unaryTokens) {
        return true
    }

    // handle string literals
    for (quote in stringsQuotes) {
        if (type == quote) {
            return getOrNull(otherIdx - 1) == quote
                    && getOrNull(otherIdx - 2) != '\\'
                    && otherIdx - 1 != typeIdx
        }
    }

    // use the standard character type check
    return type.category != other.category
}