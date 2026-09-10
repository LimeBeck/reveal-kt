import com.microsoft.playwright.Playwright
import com.microsoft.playwright.Page
import org.apache.pdfbox.Loader
import java.net.ServerSocket
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.*
import kotlin.test.*

class FirstUseIntegrationTest {
    private val root = Files.createTempDirectory("revealkt-first-use ")
    private val home = root.resolve("empty-home").createDirectories()
    private val jar = Path(System.getProperty("revealkt.cli.jar")).copyTo(root.resolve("revealkt.jar"))
    private val java = Path(System.getProperty("java.home"), "bin", "java").toString()

    private fun cli(vararg arguments: String, success: Boolean = true, environment: Map<String, String> = emptyMap()): String {
        val log = Files.createTempFile(root, "command-", ".log")
        val process = ProcessBuilder(
            java, "-Djava.awt.headless=true", "-Duser.home=$home", "-Dmaven.repo.local=${home.resolve("maven")}",
            "-jar", jar.toString(), *arguments
        ).apply {
            directory(root.toFile())
            redirectErrorStream(true)
            redirectOutput(log.toFile())
            environment().remove("CLASSPATH")
            environment().putAll(environment)
        }.start()
        try {
            assertTrue(process.waitFor(120, TimeUnit.SECONDS), "CLI timeout: ${log.readText()}")
            val output = log.readText()
            assertEquals(success, process.exitValue() == 0, output)
            return output
        } finally {
            process.destroyForcibly()
        }
    }

    private fun Page.recordAndBlockRemoteRequests(): List<String> {
        val requests = mutableListOf<String>()
        onRequest { request ->
            if (request.url().startsWith("http")) requests += request.url()
        }
        route(Regex("^https?://.*").toPattern()) { it.abort() }
        return requests
    }

    @Test
    fun `guide and all shipped examples work with only a copied CLI jar`() {
        val examples = mapOf("starter" to 2, "technical" to 4, "lesson" to 3, "custom-theme" to 3)
        Playwright.create(Playwright.CreateOptions().setEnv(mapOf("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD" to "1"))).use { playwright ->
            playwright.chromium().launch().use { browser ->
                for ((example, pages) in examples) {
                    val project = "Deck-$example"
                    cli("init", project, "--example", example, "--dirname", "Project $example")
                    val script = "Project $example/presentation/$project.reveal.kts"
                    assertContains(cli("doctor", script, "--output-dir", "Export $example"), "Environment ready")
                    assertFalse(root.resolve("Export $example").exists(), "Doctor should not create the output directory")
                    cli("bundle", script, "--output-dir", "Export $example")
                    val page = browser.newPage()
                    try {
                        val remoteRequests = page.recordAndBlockRemoteRequests()
                        page.onPageError { error(it) }
                        page.navigate(root.resolve("Export $example/index.html").toUri().toString())
                        page.waitForFunction("() => window.revealKtReady === true")
                        assertEquals(pages, page.locator(".slides > section").count())
                        assertTrue(remoteRequests.isEmpty(), "Example requested remote resources: $remoteRequests")
                        when (example) {
                            "technical" -> assertTrue(page.locator("pre code .hljs-keyword").count() > 0)
                            "lesson" -> {
                                page.evaluate("() => window.revealKtDeck.slide(1)")
                                assertEquals(0, page.locator("section.present .fragment.visible").count())
                                page.evaluate("() => window.revealKtDeck.nextFragment()")
                                assertEquals(1, page.locator("section.present .fragment.visible").count())
                            }
                            "custom-theme" -> {
                                assertEquals("rgb(16, 44, 53)", page.evaluate("() => getComputedStyle(document.body).backgroundColor"))
                                assertEquals(true, page.evaluate("() => document.images[0].naturalWidth > 0"))
                            }
                        }
                    } finally {
                        page.close()
                    }
                    val port = ServerSocket(0).use { it.localPort }
                    cli("pdf", script, "--port", "$port", "-o", "$project.pdf")
                    Loader.loadPDF(root.resolve("$project.pdf").toFile()).use { assertEquals(pages, it.numberOfPages, example) }
                }
            }
        }
        assertFalse(home.resolve(".gradle").exists())
        assertFalse(home.resolve(".m2").exists())
        assertFalse(home.resolve("maven").exists())
    }

    @Test
    fun `math plugin is requested for formulas but not code snippets`() {
        cli("init", "Math")
        val script = root.resolve("Math/presentation/Math.reveal.kts")
        val cases = mapOf(
            "+code { \"price = ${'$'}10\" }" to false,
            "+regularText { \"\\\\(x^2\\\\)\" }" to true,
        )
        Playwright.create(Playwright.CreateOptions().setEnv(mapOf("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD" to "1"))).use { playwright ->
            playwright.chromium().launch().use { browser ->
                for ((element, expectsMath) in cases) {
                    script.writeText("""
                        import dev.limebeck.revealkt.dsl.*
                        import dev.limebeck.revealkt.dsl.slides.*
                        slides { regularSlide { $element } }
                    """.trimIndent())
                    cli("bundle", script.toString(), "--output-dir", "math-html")
                    val page = browser.newPage()
                    try {
                        val requests = page.recordAndBlockRemoteRequests()
                        page.navigate(root.resolve("math-html/index.html").toUri().toString())
                        page.waitForFunction("() => window.revealKtReady === true")
                        assertEquals(expectsMath, page.evaluate("() => 'mathjax2' in window.revealKtDeck.getPlugins()"))
                        if (expectsMath) page.waitForCondition { requests.any { it.contains("MathJax.js") } }
                        assertEquals(expectsMath, requests.any { it.contains("MathJax.js") }, requests.toString())
                    } finally {
                        page.close()
                    }
                }
            }
        }
    }

    @Test
    fun `doctor diagnoses missing prerequisites without executing the script`() {
        val script = root.resolve("Unsafe.reveal.kts")
        script.writeText("java.io.File(\"executed.txt\").writeText(\"should never happen\")")
        val missingBrowsers = root.resolve("missing-browsers")
        val output = cli("doctor", script.toString(), success = false,
            environment = mapOf("PLAYWRIGHT_BROWSERS_PATH" to missingBrowsers.toString()))
        assertContains(output, "[FAIL] Chromium")
        assertContains(output, "chrome install")
        assertContains(output, "not executed")
        assertFalse(root.resolve("executed.txt").exists())
        assertFalse(missingBrowsers.exists(), "Doctor must not download browsers")
        val conflictingFile = root.resolve("file-not-directory").apply { writeText("keep") }
        val badPaths = cli("doctor", "absent.reveal.kts", "--output-dir", "$conflictingFile/out", success = false)
        assertContains(badPaths, "Check the script path")
        assertContains(badPaths, "Choose a writable directory")
        assertEquals("keep", conflictingFile.readText())
    }

    @Test
    fun `diagnostics show compilation and runtime locations and init preserves existing files`() {
        cli("init", "Diagnostic")
        val script = root.resolve("Diagnostic/presentation/Diagnostic.reveal.kts")
        val original = script.readText()
        assertContains(cli("init", "Diagnostic", success = false), "Choose another --dirname")
        assertEquals(original, script.readText())
        script.writeText("\nunknownSymbolForDiagnostic\n")
        val compilation = cli("bundle", script.toString(), success = false)
        assertContains(compilation, "Diagnostic.reveal.kts:2:1: error:")
        assertContains(compilation, "unknownSymbolForDiagnostic")
        script.writeText("\nerror(\"runtime diagnostic marker\")\n")
        val execution = cli("bundle", script.toString(), success = false)
        assertContains(execution, "Diagnostic.reveal.kts:2: error:")
        assertContains(execution, "runtime diagnostic marker")
    }
}
