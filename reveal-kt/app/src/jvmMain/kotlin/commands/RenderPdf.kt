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
import com.microsoft.playwright.CLI
import dev.limebeck.application.pdf.PdfRenderer
import dev.limebeck.application.server.Config
import dev.limebeck.application.server.ServerConfig
import dev.limebeck.application.server.runServer
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

class RenderPdf : CliktCommand(name = "pdf", help = "Render pdf from presentation") {
    val port: Int by option("-p", "--port", help = "Port").int().default(8080)
    val host: String by option("-h", "--host", help = "Host").default("0.0.0.0")
    val basePath: Path? by option("-b", help = "Script dir").path()
    val script: File by argument(help = "Script file").file(canBeDir = false, mustBeReadable = true)
    val output: File by option("-o", "--output", help = "Output file")
        .file(canBeDir = false)
        .default(File("./output.pdf"))

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

    val renderer = PdfRenderer()

    override fun run() {
        runServer(
            Config(
                server = ServerConfig(host, port),
                basePath = basePath?.pathString ?: script.parent,
                script = script
            ),
            background = false
        )
        runBlocking {
            val outputData = renderer.render("http://$host:$port/?print-pdf")
            output.writeBytes(outputData)
        }
    }
}

class Chrome: CliktCommand(name = "chrome", help = "Manage chrome installation") {
    override fun run() {}
}

class InstallChrome : CliktCommand(name = "install", help = "Install chrome") {
    override fun run() {
        CLI.main(arrayOf("install", "chromium"))
    }
}

class UninstallChrome : CliktCommand(name = "uninstall", help = "Uninstall chrome") {
    override fun run() {
        CLI.main(arrayOf("uninstall"))
    }
}
