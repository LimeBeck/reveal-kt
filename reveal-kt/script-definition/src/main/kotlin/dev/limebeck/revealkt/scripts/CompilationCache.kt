package dev.limebeck.revealkt.scripts

import dev.limebeck.revealkt.scripts.tools.Directories
import java.io.File
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.dependencies
import kotlin.script.experimental.jvm.JvmDependency


const val COMPILED_SCRIPTS_CACHE_DIR_ENV_VAR = "KOTLIN_REVEAL_KT_COMPILED_SCRIPTS_CACHE_DIR"
const val COMPILED_SCRIPTS_CACHE_DIR_PROPERTY = "kotlin.reveal.kt.compiled.scripts.cache.dir"
const val COMPILED_SCRIPTS_CACHE_VERSION = 2

internal fun findCacheBaseDir(): File? {
    val cacheExtSetting = System.getProperty(COMPILED_SCRIPTS_CACHE_DIR_PROPERTY)
        ?: System.getenv(COMPILED_SCRIPTS_CACHE_DIR_ENV_VAR)
    return when {
        cacheExtSetting == null -> Directories(System.getProperties(), System.getenv()).cache
            ?.takeIf { it.exists() && it.isDirectory }
            ?.let { File(it, "reveal.kt.compiled.cache").apply { mkdir() } }

        cacheExtSetting.isBlank() -> null
        else -> File(cacheExtSetting)
    }?.takeIf { it.exists() && it.isDirectory }
}

internal fun compiledScriptUniqueName(
    script: SourceCode,
    scriptCompilationConfiguration: ScriptCompilationConfiguration
): String {
    val digestWrapper = MessageDigest.getInstance("SHA-256")

    fun addToDigest(chunk: String) = with(digestWrapper) {
        val chunkBytes = chunk.toByteArray()
        update(chunkBytes.size.toByteArray())
        update(chunkBytes)
    }

    digestWrapper.update(COMPILED_SCRIPTS_CACHE_VERSION.toByteArray())
    addToDigest(script.text)
    scriptCompilationConfiguration.notTransientData.entries
        .sortedBy { it.key.name }
        .forEach {
            addToDigest(it.key.name)
            addToDigest(it.value.toString())
        }
    // Classpath paths stay the same when RevealKt is upgraded in place (e.g. revealkt.jar),
    // so the cache key also covers the content version of every dependency.
    scriptCompilationConfiguration[ScriptCompilationConfiguration.dependencies].orEmpty()
        .filterIsInstance<JvmDependency>()
        .flatMap { it.classpath }
        .flatMap { it.contentFiles() }
        .forEach { addToDigest("${it.path}:${it.length()}:${it.lastModified()}") }
    return digestWrapper.digest().toHexString() + ".jar"
}

private fun File.contentFiles(): List<File> =
    if (isDirectory) walk().filter { it.isFile }.sortedBy { it.path }.toList() else listOf(this)

private fun ByteArray.toHexString(): String = joinToString("", transform = { "%02x".format(it) })

private fun Int.toByteArray() = ByteBuffer.allocate(Int.SIZE_BYTES).also { it.putInt(this) }.array()