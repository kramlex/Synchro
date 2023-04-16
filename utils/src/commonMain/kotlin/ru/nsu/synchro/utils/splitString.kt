package ru.nsu.synchro.utils

fun splitString(inputString: String, separator: String = "###"): Pair<String, String> {
    val index = inputString.indexOf(separator)
    if (index == -1) {
        throw IllegalArgumentException("Input string does not contain delimiter ###")
    }
    return Pair(inputString.substring(0, index), inputString.substring(index + 3))
}
