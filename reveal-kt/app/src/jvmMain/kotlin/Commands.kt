package dev.limebeck.application

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import dev.limebeck.application.server.Config
import dev.limebeck.application.server.ServerConfig
import dev.limebeck.application.server.runServer
import dev.limebeck.application.templates.generatePresentationTemplate
import dev.limebeck.revealkt.RevealkConfig
import java.io.File
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.pathString

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
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true) }
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