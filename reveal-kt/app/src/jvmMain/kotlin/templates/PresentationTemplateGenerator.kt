package dev.limebeck.application.templates

import dev.limebeck.revealkt.RevealkConfig
import java.nio.file.Path
import java.security.InvalidParameterException

val resources = listOf(
    "/template/presentation/assets/image.png",
    "/template/presentation/{{basename}}.reveal.kts",
    "/template/build.gradle.kts",
    "/template/settings.gradle.kts",
)

class ResourceLoader

fun loadResource(name: String) = ResourceLoader::class.java.getResourceAsStream(name)
    ?: throw InvalidParameterException("<73e3046b> Resource $name is not found in classpath")

val substitutionRegex = "\\{\\{\\s*(\\w+)\\s*\\}\\}".toRegex()

fun substitute(text: String, values: Map<String, String>) = text.replace(substitutionRegex) {
    val valueName = it.groupValues[1]
    values[valueName] ?: throw InvalidParameterException("<dd880bdb> Can`t substitute value ${valueName}. Text: $text")
}

fun generatePresentationTemplate(name: String, targetDir: Path) {
    val substitutionValues = mapOf(
        "basename" to name,
        "version" to RevealkConfig.version
    )

    targetDir.toFile().mkdirs()

    resources.forEach { resourcePath ->
        val targetPath = targetDir.resolve(
            "./" + substitute(resourcePath.removePrefix("/template/"), substitutionValues)
        )
        targetPath.parent.toFile().mkdirs()
        val resource = loadResource(resourcePath)
        if (resourcePath.endsWith(".kts")) {
            val textFileData = resource.readAllBytes().decodeToString()
            val processed = substitute(textFileData, substitutionValues)
            targetPath.toFile().writeText(processed, charset = Charsets.UTF_8)
        } else {
            val bytes = resource.readAllBytes()
            targetPath.toFile().createNewFile()
            targetPath.toFile().outputStream().use {
                it.write(bytes)
            }
        }
    }
}