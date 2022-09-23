package org.octopusden.utils

fun Int.offsetFormat(minLength: Int): String {
    val stringRepresentation = this.toString()
    val offset = minLength - stringRepresentation.length
    return if (offset <= 0) {
        stringRepresentation
    } else {
        val sb = StringBuffer()
        repeat(offset) { sb.append('0') }
        sb.append(stringRepresentation).toString()
    }
}