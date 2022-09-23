package org.octopusden.utils

fun String.expandContext(context: Map<String, String>, expandEnv: Boolean): String {
    if (this.indexOf('$') == -1) return this

    val sb = StringBuilder()
    var left = 0
    var varStart = 0

    while (this.indexOf('$', left).also { varStart = it } != -1) {
        sb.append(this.substring(left, varStart++))

        val varKey = if (this[varStart] == '{') {
            val varEnd = this.indexOf('}', varStart)
            this.substring(++varStart until varEnd).also { left = varEnd + 1 }
        } else {
            val varEnd = with(this.indexOf(varStart) { !it.isLetterOrDigit() }) {
                if (this != -1) this else this@expandContext.length
            }
            this.substring(varStart until varEnd).also { left = varEnd }
        }
        if (varKey.startsWith("env.")) {
            if (expandEnv) {
                sb.append(System.getenv(varKey.replaceFirst("env.", "")))
            } else {
                sb.append("\${").append(varKey).append("}")
            }
        } else {
            sb.append(context[varKey])
        }
    }

    sb.append(this.substring(left))
    return sb.toString()
}

fun String.expandContext(context: Map<String, String>): String = expandContext(context, false)

inline fun String.indexOf(start: Int = 0, predicate: (Char) -> Boolean): Int =
        (start until this.length).firstOrNull { predicate(this[it]) } ?: -1
