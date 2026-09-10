package dev.limebeck.application.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

internal data class HealthCheck(val name: String, val problem: String? = null, val action: String? = null) {
    val passed: Boolean get() = problem == null

    fun format(): String = if (passed) "[OK] $name" else "[FAIL] $name: $problem\n  $action"
}

internal fun checkOutputDirectory(path: Path): HealthCheck {
    val output = path.toAbsolutePath().normalize()
    val parent = generateSequence(output) { it.parent }.firstOrNull { it.exists() }
    return try {
        require(parent != null && parent.isDirectory()) { "an existing parent is not a directory" }
        val probe = Files.createTempFile(parent, ".revealkt-doctor-", ".tmp")
        try {
            probe.writeText("RevealKt write check")
        } finally {
            probe.deleteIfExists()
        }
        HealthCheck("Output directory: $output")
    } catch (error: Exception) {
        HealthCheck("Output directory: $output", error.message ?: error.toString(), "Choose a writable directory with --output-dir or fix its permissions.")
    }
}

class Doctor : CliktCommand(name = "doctor") {
    override fun help(context: Context) = "Check Java, Chromium and presentation directories without executing the script"

    private val script: Path? by argument(help = "Optional presentation script").path().optional()
    private val basePath: Path? by option("--base-path", help = "Resource directory, as for run --base-path").path()
    private val outputDir: Path by option("--output-dir", help = "Directory to check for exports").path().default(Path("out"))

    override fun run() {
        val invocation = Doctor::class.java.protectionDomain.codeSource.location.toURI().let { uri ->
            val path = Path.of(uri)
            if (path.extension == "jar") "java -jar \"$path\"" else "revealkt"
        }
        val javaVersion = Runtime.version().feature()
        val javaCheck = if (javaVersion >= 21) HealthCheck("Java $javaVersion") else HealthCheck(
            "Java $javaVersion", "Java 21 or newer is required", "Install JDK 21 or 25 and select it through JAVA_HOME and PATH."
        )
        val checks = buildList {
            add(javaCheck)
            add(checkOutputDirectory(outputDir))
            val normalizedScript = script?.toAbsolutePath()?.normalize()
            if (normalizedScript != null) {
                add(if (normalizedScript.isRegularFile() && normalizedScript.isReadable()) {
                    HealthCheck("Script: $normalizedScript (not executed)")
                } else {
                    HealthCheck("Script: $normalizedScript", "file is missing or unreadable", "Check the script path and read permissions.")
                })
            }
            val resources = (basePath ?: normalizedScript?.parent ?: Path(".")).toAbsolutePath().normalize()
            add(if (resources.isDirectory() && resources.isReadable()) HealthCheck("Resource directory: $resources") else {
                HealthCheck("Resource directory: $resources", "directory is missing or unreadable", "Use an existing directory with --base-path.")
            })
            val assets = resources.resolve("assets")
            add(when {
                !assets.exists() -> HealthCheck("Assets: $assets (optional; create it when adding images or CSS)")
                assets.isDirectory() && assets.isReadable() -> HealthCheck("Assets: $assets")
                else -> HealthCheck("Assets: $assets", "expected a readable directory", "Move conflicting files and create an assets directory.")
            })
            add(checkChromium(invocation))
        }
        checks.forEach { echo(it.format()) }
        if (javaVersion !in setOf(21, 25)) echo("[NOTE] The CI Java versions are 21 and 25.")
        echo("[NOTE] MathJax and any remote presentation assets need network access; doctor does not check these URLs.")
        if (checks.any { !it.passed }) throw CliktError("Environment needs attention. Follow the actions above and run doctor again.")
        echo("Environment ready for preview, HTML and PDF export.")
    }

    private fun checkChromium(invocation: String): HealthCheck = try {
        val options = Playwright.CreateOptions().setEnv(mapOf("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD" to "1"))
        Playwright.create(options).use { playwright ->
            val executable = Path(playwright.chromium().executablePath())
            if (!executable.isRegularFile()) {
                HealthCheck("Chromium", "not installed at $executable", "Run: $invocation chrome install")
            } else {
                playwright.chromium().launch(BrowserType.LaunchOptions().setTimeout(10_000.0)).use { browser ->
                    HealthCheck("Chromium ${browser.version()}: $executable (launch succeeded)")
                }
            }
        }
    } catch (error: Exception) {
        HealthCheck(
            "Chromium", error.message ?: error.toString(),
            "Run: $invocation chrome install. On Ubuntu, install system libraries with: $invocation chrome install --with-deps"
        )
    }
}
