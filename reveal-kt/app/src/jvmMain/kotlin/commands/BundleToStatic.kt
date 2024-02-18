package dev.limebeck.application.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import dev.limebeck.application.getResourcesList
import dev.limebeck.application.isFont
import dev.limebeck.application.server.logger
import dev.limebeck.application.server.renderLoadResult
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import java.io.File
import java.nio.file.Path
import kotlin.io.path.*

class BundleToStatic : CliktCommand(name = "bundle", help = "Bundle to static html file") {
    val outputDir: Path? by option(help = "Output dir").path(
        mustBeWritable = true,
        canBeDir = true,
        canBeFile = false
    )
    val script: File by argument(help = "Script file").file(canBeDir = false, mustBeReadable = true)

    init {
        context {
            helpFormatter = {
                MordantHelpFormatter(
                    showDefaultValues = true,
                    context = it
                )
            }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    override fun run() {
        val scriptLoader = RevealKtScriptLoader()
        when (val loadResult = scriptLoader.loadScript(script)) {
            is RevealKtScriptLoader.LoadResult.Success -> {
                val outputDir = outputDir ?: Path.of("./out")
                if (outputDir.notExists()) {
                    outputDir.createDirectories()
                }

                val resources = getResourcesList("js").filter {
                    it.isFont() || it.name in listOf("revealkt.js", "favicon.ico")
                }

                resources.forEach {
                    it.copyTo(outputDir.resolve(it.name))
                }

                val assetsPath = script.parentFile.resolve("assets").toPath()
                if (assetsPath.exists()) {
//                    println("<4a6f2742> Copy assets from $assetsPath to $outputDir")
                    assetsPath.copyToRecursively(outputDir.resolve("assets"), followLinks = true, overwrite = true)
                }

                val result = renderLoadResult(loadResult)
                outputDir.resolve("index.html")
                    .createFile()
                    .writeText(result)
            }

            is RevealKtScriptLoader.LoadResult.Error -> {
                loadResult.diagnostic.forEach {
                    logger.error("$it")
                }
            }
        }
    }
}