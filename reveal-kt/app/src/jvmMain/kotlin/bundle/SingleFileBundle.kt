package dev.limebeck.application.bundle

import java.nio.file.Path
import java.util.Base64
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readBytes

private val mimeTypes = mapOf(
    "png" to "image/png", "jpg" to "image/jpeg", "jpeg" to "image/jpeg", "gif" to "image/gif",
    "svg" to "image/svg+xml", "webp" to "image/webp", "avif" to "image/avif", "ico" to "image/x-icon",
    "mp4" to "video/mp4", "webm" to "video/webm", "ogv" to "video/ogg", "mp3" to "audio/mpeg",
    "ogg" to "audio/ogg", "wav" to "audio/wav", "css" to "text/css", "js" to "text/javascript",
    "json" to "application/json", "woff" to "font/woff", "woff2" to "font/woff2", "ttf" to "font/ttf",
    "otf" to "font/otf", "pdf" to "application/pdf", "txt" to "text/plain",
)

private val assetAttribute =
    Regex("""(\s(?:src|href|poster|data-src|data-background-image|data-background-video)=")(assets/[^"]+)(")""")
private val assetConfiguration = Regex(""""(cssLink|parallaxBackgroundImage)":"(assets/[^"\\]+)"""")
private val runtimeScript = Regex("""<script[^>]*\ssrc="revealkt\.js"[^>]*></script>""")
private val cssUrl = Regex("""url\(\s*(['"]?)([^'")]+)\1\s*\)""")

/** A presentation rendered as one HTML file; [missing] lists referenced assets that were not found. */
class SingleFileResult(val html: String, val missing: List<String>)

/**
 * Embeds the runtime script and every `assets/` file the page references as data URLs, so the
 * presentation opens from a single file without a server. Stylesheets have their relative `url()`
 * references embedded as well. Remote URLs stay remote.
 */
fun bundleSingleFile(html: String, runtimeJs: ByteArray, assetsDir: Path): SingleFileResult {
    val missing = linkedSetOf<String>()

    fun dataUrl(file: Path, depth: Int = 0): String {
        val mime = mimeTypes[file.extension.lowercase()] ?: "application/octet-stream"
        val bytes = if (mime == "text/css" && depth < 4) {
            inlineCssUrls(file.readBytes().decodeToString(), file.parent, depth, missing, ::dataUrl).encodeToByteArray()
        } else {
            file.readBytes()
        }
        return "data:$mime;base64," + Base64.getEncoder().encodeToString(bytes)
    }

    fun embed(reference: String): String? {
        val relative = reference.removePrefix("assets/").substringBefore('#').substringBefore('?')
        val file = assetsDir.resolve(relative).normalize()
        if (!file.startsWith(assetsDir.normalize()) || !file.isRegularFile()) {
            missing += reference
            return null
        }
        return dataUrl(file)
    }

    val scriptTag = "<script src=\"data:text/javascript;base64,${Base64.getEncoder().encodeToString(runtimeJs)}\"></script>"
    val withRuntime = runtimeScript.replace(html) { scriptTag }
    check(withRuntime != html) { "The page does not reference revealkt.js" }

    val withAttributes = assetAttribute.replace(withRuntime) { match ->
        val (prefix, reference, suffix) = match.destructured
        embed(unescapeHtml(reference))?.let { prefix + it + suffix } ?: match.value
    }
    val withConfiguration = assetConfiguration.replace(withAttributes) { match ->
        val (key, reference) = match.destructured
        embed(reference)?.let { "\"$key\":\"$it\"" } ?: match.value
    }
    return SingleFileResult(withConfiguration, missing.toList())
}

private fun inlineCssUrls(
    css: String,
    directory: Path,
    depth: Int,
    missing: MutableSet<String>,
    dataUrl: (Path, Int) -> String,
): String = cssUrl.replace(css) { match ->
    val reference = match.groupValues[2].trim()
    if (Regex("^([a-zA-Z][a-zA-Z0-9+.-]*:|/|#)").containsMatchIn(reference)) return@replace match.value
    val file = directory.resolve(reference.substringBefore('#').substringBefore('?')).normalize()
    if (!file.exists() || !file.isRegularFile()) {
        missing += reference
        match.value
    } else {
        "url(\"${dataUrl(file, depth + 1)}\")"
    }
}

private fun unescapeHtml(value: String) = value
    .replace("&quot;", "\"").replace("&#39;", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&")
