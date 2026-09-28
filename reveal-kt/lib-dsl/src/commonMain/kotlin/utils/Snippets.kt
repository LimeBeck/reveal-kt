package dev.limebeck.revealkt.utils

private val regionMarker = Regex("""^\s*(//|#|--|/\*|<!--)\s*(end)?region\b\s*(\S*)""", RegexOption.IGNORE_CASE)

/**
 * Selects the part of a source file shown on a slide.
 *
 * [region] picks the lines between `// region name` and `// endregion` markers (`#`, `--`, block and
 * HTML comments also work); [range] picks 1-based line numbers. Region markers are dropped from the result
 * and the common indentation is removed.
 */
fun extractSnippet(text: String, region: String? = null, range: IntRange? = null, source: String = "file"): String {
    var lines = text.lines()
    if (region != null) {
        val start = lines.indexOfFirst { line ->
            regionMarker.find(line)?.let { it.groupValues[2].isEmpty() && it.groupValues[3] == region } == true
        }
        require(start >= 0) { "Region '$region' is not found in $source" }
        var depth = 0
        val end = (start + 1 until lines.size).firstOrNull { index ->
            val marker = regionMarker.find(lines[index])
            when {
                marker == null -> false
                marker.groupValues[2].isEmpty() -> { depth++; false }
                depth == 0 -> true
                else -> { depth--; false }
            }
        }
        requireNotNull(end) { "Region '$region' in $source has no matching endregion marker" }
        lines = lines.subList(start + 1, end)
    }
    if (range != null) {
        require(range.first >= 1 && range.last <= lines.size && !range.isEmpty()) {
            "Line range $range is outside $source, which has ${lines.size} lines"
        }
        lines = lines.subList(range.first - 1, range.last)
    }
    return lines.filterNot { regionMarker.containsMatchIn(it) }.joinToString("\n").trimIndent()
}

private val languagesByExtension = mapOf(
    "kt" to "kotlin", "kts" to "kotlin", "java" to "java", "scala" to "scala", "groovy" to "groovy",
    "gradle" to "groovy", "js" to "javascript", "mjs" to "javascript", "ts" to "typescript",
    "py" to "python", "rb" to "ruby", "go" to "go", "rs" to "rust", "c" to "c", "h" to "c",
    "cpp" to "cpp", "cs" to "csharp", "swift" to "swift", "sh" to "bash", "bash" to "bash",
    "sql" to "sql", "json" to "json", "yaml" to "yaml", "yml" to "yaml", "toml" to "ini",
    "xml" to "xml", "html" to "html", "css" to "css", "md" to "markdown", "proto" to "protobuf",
)

/** highlight.js language for a file name, or null when unknown. */
fun languageForFile(name: String): String? =
    languagesByExtension[name.substringAfterLast('/').substringAfterLast('.', "").lowercase()]
