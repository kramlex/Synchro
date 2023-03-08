package ru.nsu.synchro.msecd.token

internal class MultiplatformStringTokenizer(
    private val str: String,
    private var delimiters: String? = " \t\n\r\u000c",
    private val retDelims: Boolean = false,
) : StringTokenizer {

    private var currentPosition = 0
    private var newPosition: Int
    private val maxPosition: Int
    private var delimsChanged = false
    private var maxDelimCodePoint = 0
    private var hasSurrogates = false
    private var delimiterCodePoints: MutableList<Int> = mutableListOf()

    private fun setMaxDelimCodePoint() {
        val delimiters = delimiters
        if (delimiters == null) {
            maxDelimCodePoint = 0
            return
        }
        var m = 0
        var c: Int
        var count = 0
        var index = 0
        while (index < delimiters.length) {
            c = delimiters[index].code
            if (c >= MIN_HIGH_SURROGATE.code && c <= MAX_LOW_SURROGATE.code) {
                c = delimiters.getCodePointAt(index)
                hasSurrogates = true
            }
            if (m < c) m = c
            count++
            index += charCount(c)
        }
        maxDelimCodePoint = m
        if (hasSurrogates) {
            delimiterCodePoints = List(count) { 0 }.toMutableList()
            var secondIndex = 0
            var j = 0
            while (secondIndex < count) {
                c = delimiters.getCodePointAt(j)
                delimiterCodePoints[secondIndex] = c
                secondIndex++
                j += charCount(c)
            }
        }
    }

    private fun charCount(codePoint: Int): Int {
        return if (codePoint >= MIN_SUPPLEMENTARY_CODE_POINT) 2 else 1
    }

    init {
        newPosition = -1
        maxPosition = str.length
        setMaxDelimCodePoint()
    }

    private fun skipDelimiters(startPos: Int): Int {
        if (delimiters == null) throw NullPointerException()
        var position = startPos
        while (!retDelims && position < maxPosition) {
            if (!hasSurrogates) {
                val c = str[position]
                if (c.code > maxDelimCodePoint || delimiters!!.indexOf(c) < 0) break
                position++
            } else {
                val c: Int = str.getCodePointAt(position)
                if (c > maxDelimCodePoint || !isDelimiter(c)) {
                    break
                }
                position += charCount(c)
            }
        }
        return position
    }

    private fun scanToken(startPos: Int): Int {
        var position = startPos
        while (position < maxPosition) {
            if (!hasSurrogates) {
                val c = str[position]
                if (c.code <= maxDelimCodePoint && delimiters!!.indexOf(c) >= 0) break
                position++
            } else {
                val c: Int = str.getCodePointAt(position)
                if (c <= maxDelimCodePoint && isDelimiter(c)) break
                position += charCount(c)
            }
        }
        if (retDelims && startPos == position) {
            if (!hasSurrogates) {
                val c = str[position]
                if (c.code <= maxDelimCodePoint && delimiters!!.indexOf(c) >= 0) position++
            } else {
                val c: Int = str.getCodePointAt(position)
                if (c <= maxDelimCodePoint && isDelimiter(c)) position += charCount(c)
            }
        }
        return position
    }

    private fun isDelimiter(codePoint: Int): Boolean {
        for (delimiterCodePoint in delimiterCodePoints) {
            if (delimiterCodePoint == codePoint) {
                return true
            }
        }
        return false
    }

    override fun hasMoreTokens(): Boolean {
        /*
         * Temporarily store this position and use it in the following
         * nextToken() method only if the delimiters haven't been changed in
         * that nextToken() invocation.
         */
        newPosition = skipDelimiters(currentPosition)
        return newPosition < maxPosition
    }

    override fun nextToken(): String {
        /*
         * If next position already computed in hasMoreElements() and
         * delimiters have changed between the computation and this invocation,
         * then use the computed value.
         */
        currentPosition = if (newPosition >= 0 && !delimsChanged) newPosition else skipDelimiters(currentPosition)

        /* Reset these anyway */delimsChanged = false
        newPosition = -1
        if (currentPosition >= maxPosition) throw NoSuchElementException()
        val start = currentPosition
        currentPosition = scanToken(currentPosition)
        return str.substring(start, currentPosition)
    }

    fun nextToken(delim: String?): String {
        delimiters = delim

        /* delimiter string specified, so set the appropriate flag. */delimsChanged = true
        setMaxDelimCodePoint()
        return nextToken()
    }

    fun hasMoreElements(): Boolean {
        return hasMoreTokens()
    }

    fun nextElement(): Any {
        return nextToken()
    }

    fun countTokens(): Int {
        var count = 0
        var currpos = currentPosition
        while (currpos < maxPosition) {
            currpos = skipDelimiters(currpos)
            if (currpos >= maxPosition) break
            currpos = scanToken(currpos)
            count++
        }
        return count
    }

    companion object {
        const val MIN_HIGH_SURROGATE = '\uD800'
        const val MAX_LOW_SURROGATE = '\uDFFF'
        const val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000
    }
}
