package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.elements.Code
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import dev.limebeck.revealkt.utils.extractSnippet
import dev.limebeck.revealkt.utils.languageForFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readBytes
import kotlin.io.path.readText

actual class AssetLoader(
    val assetPath: String,
    /** Directory that [codeFromFile] resolves relative paths against, normally the script directory. */
    val sourceRoot: String = Path(assetPath).toAbsolutePath().parent?.toString() ?: ".",
) {
    private val readFiles = linkedSetOf<Path>()

    /** Files read by this loader; the preview server reloads when they change. */
    val loadedFiles: Set<Path> get() = readFiles.toSet()

    actual fun loadAsset(path: String): ByteArray = track(Path(assetPath, path)).readBytes()

    /**
     * Shows code from a real source file, so slides stay in sync with it.
     *
     * @param path file path, relative to the script directory or absolute
     * @param region name of a `// region name` … `// endregion` block to show
     * @param range 1-based line numbers to show (within the region, if one is given)
     * @param lang highlight.js language; inferred from the file extension by default
     * @param lineNumbers Reveal.js line highlighting, for example `"1,3-4"` or `"|2|3"`
     */
    fun codeFromFile(
        path: String,
        region: String? = null,
        range: IntRange? = null,
        lang: String? = languageForFile(path),
        lineNumbers: String? = null,
        trim: Boolean = true,
        id: ID = UuidGenerator.generateId(),
    ): Code {
        val file = track(Path(sourceRoot).resolve(path))
        val snippet = extractSnippet(file.readText(), region, range, source = file.toString())
        return Code(id = id, trim = trim, lang = lang, lines = lineNumbers, code = snippet)
    }

    private fun track(path: Path): Path = path.toAbsolutePath().normalize().also { readFiles.add(it) }
}
