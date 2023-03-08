package ru.nsu.synchro.msecd.token

expect fun createStringTokenizer(
    string: String,
    separators: String,
    returnDelims: Boolean
): StringTokenizer

interface StringTokenizer {
    fun hasMoreTokens(): Boolean
    fun nextToken(): String
}
