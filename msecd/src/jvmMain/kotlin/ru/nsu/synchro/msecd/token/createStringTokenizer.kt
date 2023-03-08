package ru.nsu.synchro.msecd.token

class StringTokenizerImpl(
    string: String,
    separators: String,
    returnDelims: Boolean
): StringTokenizer {
    private val javaStringTokenizer = java.util.StringTokenizer(string, separators, returnDelims)
    override fun hasMoreTokens(): Boolean = javaStringTokenizer.hasMoreTokens()

    override fun nextToken(): String = javaStringTokenizer.nextToken()
}

actual fun createStringTokenizer(
    string: String,
    separators: String,
    returnDelims: Boolean,
): StringTokenizer {
    return StringTokenizerImpl(string, separators, returnDelims)
}
