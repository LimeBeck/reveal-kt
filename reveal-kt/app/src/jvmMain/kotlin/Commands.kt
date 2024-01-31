package dev.limebeck.application

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import dev.limebeck.application.server.*
import dev.limebeck.application.templates.generatePresentationTemplate
import dev.limebeck.revealkt.RevealkConfig
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import java.io.File
import java.nio.file.Path
import kotlin.io.path.*

class RevealKtApplication : CliktCommand(
    printHelpOnEmptyArgs = true,
    help = """
           RevealKt CLI
           
           Application version ${RevealkConfig.version}
           """.trimIndent()
) {
    override fun run() = Unit
}

class RunServer : CliktCommand(name = "run", help = "Serve presentation with live-reload") {
    val port: Int by option(help = "Port").int().default(8080)
    val host: String by option(help = "Host").default("0.0.0.0")
    val basePath: Path? by option(help = "Script dir").path()
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

    override fun run() {
        runServer(
            Config(
                server = ServerConfig(host, port),
                basePath = basePath?.pathString ?: script.parent,
                script = script
            )
        )
    }
}

class RenderToFile : CliktCommand(name = "render", help = "Render to file") {
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

class InitTemplate : CliktCommand(name = "init", help = "Create presentation template") {
    val name: String by argument(help = "Presentation name")
    val basePath: Path by option(help = "Template dir")
        .path(canBeDir = true, canBeFile = false)
        .default(Path.of("."))
    val dirname: String? by option()

    override fun run() {
        generatePresentationTemplate(name, basePath / (dirname ?: name))
    }
}