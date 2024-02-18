package dev.limebeck.application.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import dev.limebeck.application.server.Config
import dev.limebeck.application.server.ServerConfig
import dev.limebeck.application.server.runServer
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

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