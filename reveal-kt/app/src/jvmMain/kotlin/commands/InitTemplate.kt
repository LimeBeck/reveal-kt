package dev.limebeck.application.commands

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import com.github.ajalt.clikt.parameters.types.choice
import dev.limebeck.application.templates.PresentationExample
import dev.limebeck.application.templates.generatePresentationTemplate
import java.nio.file.Path
import kotlin.io.path.div

class InitTemplate : CliktCommand(name = "init") {
    override fun help(context: Context) = "Create new presentation from template"

    val name: String by argument(help = "Presentation name")
    val basePath: Path by option(help = "Template dir")
        .path(canBeDir = true, canBeFile = false)
        .default(Path.of("."))
    val dirname: String? by option()

    private val example by option("--example", help = "Presentation example to generate")
        .choice(*PresentationExample.entries.map { it.option to it }.toTypedArray())
        .default(PresentationExample.STARTER)

    override fun run() {
        val target = (basePath / (dirname ?: name)).toAbsolutePath().normalize()
        try {
            generatePresentationTemplate(name, target, example)
        } catch (error: Exception) {
            throw CliktError(error.message.orEmpty())
        }
        echo("Created ${example.option} presentation: ${target.resolve("presentation/$name.reveal.kts")}")
        echo("Next: run doctor with this script, then use run, bundle or pdf. See the Getting Started guide.")
    }
}