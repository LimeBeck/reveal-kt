package dev.limebeck.application.templates

import dev.limebeck.revealkt.RevealkConfig
import java.nio.file.Path
import java.security.InvalidParameterException
import kotlin.io.path.*

enum class PresentationExample(val option: String, val assets: List<String>) {
    STARTER("starter", listOf("image.png")),
    TECHNICAL("technical", emptyList()),
    LESSON("lesson", emptyList()),
    CUSTOM_THEME("custom-theme", listOf("theme.css", "pipeline.svg")),
}

class ResourceLoader

fun loadResource(name: String) = ResourceLoader::class.java.getResourceAsStream(name)
    ?: throw InvalidParameterException("Resource $name is not found in the CLI archive")

val substitutionRegex = "\\{\\{\\s*(\\w+)\\s*\\}\\}".toRegex()

fun substitute(text: String, values: Map<String, String>) = text.replace(substitutionRegex) {
    val valueName = it.groupValues[1]
    values[valueName] ?: throw InvalidParameterException("Unknown template value: $valueName")
}

fun generatePresentationTemplate(
    name: String,
    targetDir: Path,
    example: PresentationExample = PresentationExample.STARTER,
) {
    val values = mapOf("basename" to name, "version" to RevealkConfig.version, "kotlinVersion" to RevealkConfig.kotlinVersion)
    val sourceRoot = if (example == PresentationExample.STARTER) "/template/presentation" else "/examples/${example.option}"
    val scriptResource = if (example == PresentationExample.STARTER) "{{basename}}.reveal.kts" else "presentation.reveal.kts"
    val resources = buildList {
        add("/template/build.gradle.kts" to "build.gradle.kts")
        add("/template/settings.gradle.kts" to "settings.gradle.kts")
        add("$sourceRoot/$scriptResource" to "presentation/$name.reveal.kts")
        example.assets.forEach { asset -> add("$sourceRoot/assets/$asset" to "presentation/assets/$asset") }
    }
    resources.forEach { (_, destination) ->
        require(!targetDir.resolve(destination).exists()) { "File already exists: ${targetDir.resolve(destination)}. Choose another --dirname." }
    }
    resources.forEach { (source, destination) ->
        val target = targetDir.resolve(destination)
        target.parent.createDirectories()
        loadResource(source).use { resource ->
            if (source.endsWith(".kts")) {
                target.writeText(substitute(resource.bufferedReader().readText(), values))
            } else {
                target.outputStream().use { output -> resource.copyTo(output) }
            }
        }
    }
}
