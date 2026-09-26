package dev.limebeck.application.commands

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import dev.limebeck.application.debug
import dev.limebeck.application.getResourcesList
import dev.limebeck.application.server.renderLoadResult
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import dev.limebeck.revealkt.scripts.formatDiagnostics
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import kotlin.io.path.*

class BundleToStatic : CliktCommand(name = "bundle") {
    override fun help(context: Context) = "Bundle to static html file"

    companion object {
        private val logger = LoggerFactory.getLogger("BundleToStatic")
    }

    val outputDir: Path? by option(help = "Output dir")
        .path(
            canBeDir = true,
            canBeFile = false
        )

    val basePath: Path? by option("--base-path", help = "Resource directory containing assets; defaults to the script directory")
        .path(canBeFile = false)

    val script: File by argument(help = "Script file")
        .file(canBeDir = false, mustBeReadable = true)

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
        val assetsPath = (basePath ?: script.absoluteFile.normalize().parentFile.toPath())
            .toAbsolutePath().normalize().resolve("assets")
        when (val loadResult = scriptLoader.loadScript(script, assetsPath.toFile())) {
            is RevealKtScriptLoader.LoadResult.Success -> {
                val result = renderLoadResult(loadResult)
                val outputDir = (outputDir ?: Path("out")).createDirectories()

                val resources = getResourcesList("static")
                resources.forEach { resource ->
                    logger.debug { "<ba8ede71> Copy resource ${resource.name} to $outputDir" }
                    resource.copyToRecursively(outputDir.resolve(resource.name), followLinks = false, overwrite = true)
                }

                if (assetsPath.exists()) {
                    logger.debug { "<4a6f2742> Copy assets from $assetsPath to $outputDir" }
                    assetsPath.copyToRecursively(outputDir.resolve("assets"), followLinks = true, overwrite = true)
                }

                outputDir.resolve("index.html")
                    .writeText(result)
            }

            is RevealKtScriptLoader.LoadResult.Error -> {
                throw CliktError(
                    "Failed to export ${script.absolutePath}:\n" +
                        loadResult.formatDiagnostics(script)
                )
            }
        }
    }
}
