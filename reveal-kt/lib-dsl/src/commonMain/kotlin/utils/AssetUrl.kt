package dev.limebeck.revealkt.utils

private val absoluteReference = Regex("^([a-zA-Z][a-zA-Z0-9+.-]*:|/)")

/**
 * Resolves a presentation resource: relative paths point into the `assets/` directory, while URLs
 * (`https://…`, `data:…`) and absolute paths are used as they are.
 */
fun assetUrl(path: String): String = if (absoluteReference.containsMatchIn(path)) path else "assets/$path"
