package ru.nsu.synchro.msecd

import ru.nsu.synchro.msecd.sexp.Cons
import ru.nsu.synchro.msecd.sexp.IntAtom
import ru.nsu.synchro.msecd.sexp.NumAtom
import ru.nsu.synchro.msecd.sexp.SExp
import ru.nsu.synchro.msecd.sexp.SymAtom.Companion.NIL
import ru.nsu.synchro.msecd.sexp.SymAtom
import ru.nsu.synchro.msecd.token.MultiplatformStringTokenizer
import ru.nsu.synchro.msecd.token.StringTokenizer

object LispParser {
    const val QUOTE = "quote"
    const val QUASIQUOTE = "quasiquote"
    const val UNQUOTE = "unquote"
    const val UNQUOTE_SPLICING = "unquote-splicing"
    const val LAMBDA = "lambda"
    const val MACRO = "macro"

    const val separators = "'` \t\n\r\u000c()[]{},@\""

    fun parse(s: String): SExp {
        val tokens: StringTokenizer = MultiplatformStringTokenizer(s, separators, true)
        return parse(tokens)
    }

    fun parse(tokens: StringTokenizer): SExp {
        if (!tokens.hasMoreTokens()) {
            return NIL
        }

        return when (val next = tokens.nextToken()) {
            "(" -> parseList(tokens, isFirstPosition = true)

            ")" -> NIL
            "]" -> NIL
            "NIL" -> NIL

            "\\" -> return SymAtom.LAMBDA

            "'" -> parseReaderMacro(tokens, QUASIQUOTE)

            "," -> parseReaderMacro(tokens, UNQUOTE)

            "@" -> SymAtom("@")

            "\"" -> parseString(tokens)

            else -> {
                val firstChar = next.first()
                if (firstChar == '-' && next.length > 1 || firstChar in '0'..'9') {
                    parseNumber(next)
                } else if (separators.contains(next) ) {
                    parse(tokens)
                } else {
                    SymAtom(next)
                }
            }
        }
    }

    private fun parseNumber(first: String): NumAtom {
        return IntAtom(first)
    }

    private fun parseReaderMacro(tokens: StringTokenizer, macroSymbol: String): SExp {
        val x: SExp = parse(tokens)
        return if (UNQUOTE == macroSymbol && "@" == x.toString()) {
            Cons(
                SymAtom(UNQUOTE_SPLICING),
                Cons(parse(tokens), NIL)
            )
        } else {
            Cons(macroSymbol, Cons(x, NIL))
        }
    }

    private fun parseString(tokens: StringTokenizer): SymAtom {
        var result = ""
        while (tokens.hasMoreTokens()) {
            val next = tokens.nextToken()
            result += if ("\"" == next) {
                break
            } else {
                next
            }
        }
        return SymAtom("\"" + result + "\"")
    }

    private fun parseList(tokens: StringTokenizer, isFirstPosition: Boolean): SExp {
        val first = tokens.nextToken()
        // distinguish DOT operator from pairs (x . y): 
        if (isFirstPosition && first == ".") {
            return Cons(first, parseList(tokens, false))
        }
        return when (first) {
            "(" -> Cons(parseList(tokens, true), parseList(tokens, false))

            ")" -> NIL
            NIL.stringValue -> Cons(NIL, parseList(tokens, false))
            "\\" -> Cons(SymAtom.LAMBDA, parseList(tokens, false))


            // macro symbols
            "'" -> Cons(parseReaderMacro(tokens, QUOTE), parseList(tokens, false))
            "`" -> Cons(parseReaderMacro(tokens, QUASIQUOTE), parseList(tokens, false))
            "," -> Cons(parseReaderMacro(tokens, UNQUOTE), parseList(tokens, false))

            "\"" -> Cons(parseString(tokens), parseList(tokens, false))

            "." -> {
                // skip blank
                tokens.nextToken()
                val second: SExp =  parse(tokens)
                // skip next )
                tokens.nextToken()
                return second
            }

            else -> {
                val firstChar = first.first()
                if (firstChar == '-' && first.length > 1 || (firstChar in '0'..'9')) {
                    Cons(parseNumber(first), parseList(tokens, false))
                } else if (separators.contains(first)) {
                    parseList(tokens, isFirstPosition)
                } else {
                    Cons(first, parseList(tokens, false))
                }
            }
        }
    }
}
