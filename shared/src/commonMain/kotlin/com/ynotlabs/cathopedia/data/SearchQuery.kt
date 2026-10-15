package com.ynotlabs.cathopedia.data

/** User input is always literal text, never FTS operators or column expressions. */
internal object SearchQuery {
    private val words = Regex("[\\p{L}\\p{M}\\p{N}]+")

    fun tokens(input: String): List<String> {
        val tokens = words.findAll(input.lowercase().take(500)).map { it.value }.take(32).toList()
        return tokens.map { token ->
            when {
                token in setOf("turnible", "turnibles", "thuribles", "thurable") -> "thurible"
                token == "cannon" && "law" in tokens -> "canon"
                token == "cannonlaw" -> "canon law"
                else -> token
            }
        }.flatMap { it.split(' ') }
    }

    fun match(tokens: List<String>): String = tokens.joinToString(" AND ") { "\"$it\"*" }
    fun match(input: String): String = match(tokens(input))

    /** Only repair an unknown word when a unique close title word exists. */
    fun corrected(tokens: List<String>, vocabulary: Set<String>): List<String> = tokens.map { token ->
        if (token.length < 4 || vocabulary.any { it.startsWith(token) }) token else {
            val threshold = if (token.length >= 8) 2 else 1
            val candidates = vocabulary.asSequence().filter { kotlin.math.abs(it.length - token.length) <= threshold }
                .map { it to distance(token, it) }.filter { it.second <= threshold }.toList()
            val best = candidates.minOfOrNull { it.second }
            candidates.filter { it.second == best }.singleOrNull()?.first ?: token
        }
    }

    private fun distance(a: String, b: String): Int {
        var previous = IntArray(b.length + 1) { it }
        var beforePrevious = previous
        for (i in a.indices) {
            val next = IntArray(b.length + 1)
            next[0] = i + 1
            for (j in b.indices) {
                next[j + 1] = minOf(next[j] + 1, previous[j + 1] + 1, previous[j] + if (a[i] == b[j]) 0 else 1)
                if (i > 0 && j > 0 && a[i] == b[j - 1] && a[i - 1] == b[j]) {
                    next[j + 1] = minOf(next[j + 1], beforePrevious[j - 1] + 1)
                }
            }
            beforePrevious = previous
            previous = next
        }
        return previous[b.length]
    }
}
