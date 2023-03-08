package ru.nsu.synchro.msecd.token

import cocoapods.SwiftUtils.UnicodeStringHelper

actual fun String.getCodePointAt(index: Int): Int {
    return UnicodeStringHelper().scalarCodePointWithString(
        string = this,
        index = index.toLong()
    ).toInt()
}