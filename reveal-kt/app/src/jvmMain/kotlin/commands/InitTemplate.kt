package dev.limebeck.application.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import dev.limebeck.application.templates.generatePresentationTemplate
import java.nio.file.Path
import kotlin.io.path.div

class InitTemplate : CliktCommand(name = "init", help = "Create new presentation from template") {
    val name: String by argument(help = "Presentation name")
    val basePath: Path by option(help = "Template dir")
        .path(canBeDir = true, canBeFile = false)
        .default(Path.of("."))
    val dirname: String? by option()

    override fun run() {
        generatePresentationTemplate(name, basePath / (dirname ?: name))
    }
}