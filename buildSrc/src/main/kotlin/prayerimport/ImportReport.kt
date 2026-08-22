package prayerimport

/**
 * Prints import results grouped by category, failures sorted to the bottom
 * — that list is the human's work queue (see 3b.6). Category lookup needs
 * the prayer file's `category`, not the source map, so callers pass a
 * slug->category lookup built from /content/prayers.
 */
object ImportReport {
    fun print(results: List<ImportResult>, categoryOf: (String) -> String?, out: (String) -> Unit) {
        val byCategory = results.groupBy { categoryOf(it.slug) ?: "unknown" }

        for (category in byCategory.keys.sorted()) {
            out("")
            out("== $category ==")
            byCategory.getValue(category)
                .sortedWith(compareBy({ it.slug }, { it.lang }))
                .forEach { r -> out("  ${r.slug.padEnd(30)} ${r.lang.padEnd(4)} ${r.outcome.name.lowercase()}") }
        }

        val counts = results.groupingBy { it.outcome }.eachCount()
        out("")
        out(
            "Summary: " + ImportOutcome.entries.joinToString(" · ") { o ->
                "${counts[o] ?: 0} ${o.name.lowercase()}"
            },
        )

        val failures = results.filter { it.outcome == ImportOutcome.FAILED }
        if (failures.isNotEmpty()) {
            out("")
            out("== failed (work queue) ==")
            failures.sortedWith(compareBy({ it.slug }, { it.lang })).forEach { r ->
                out("  ${r.slug}/${r.lang}: ${r.reason ?: "unknown reason"}")
            }
        }
    }
}
