import com.microsoft.playwright.Playwright
import org.apache.pdfbox.Loader
import java.net.ServerSocket
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.*
import kotlin.test.*

class CliIntegrationTest {
    private val jar = System.getProperty("revealkt.cli.jar")
    private val java = Path.of(System.getProperty("java.home"), "bin", "java").toString()

    private fun start(dir: Path, vararg args: String): Pair<Process, Path> {
        val log = Files.createTempFile(dir, "cli-", ".log")
        return ProcessBuilder(java, "-Djava.awt.headless=true", "-jar", jar, *args).directory(dir.toFile())
            .redirectErrorStream(true).redirectOutput(log.toFile()).start() to log
    }

    private fun cli(dir: Path, vararg args: String, success: Boolean = true): String {
        val (process, log) = start(dir, *args)
        try {
            assertTrue(process.waitFor(120, TimeUnit.SECONDS), "CLI timed out: ${log.readText()}")
            assertEquals(success, process.exitValue() == 0, log.readText())
            return log.readText()
        } finally {
            process.destroyForcibly()
        }
    }

    private val fixture = """
        import dev.limebeck.revealkt.core.RevealKt
        import dev.limebeck.revealkt.dsl.*
        import dev.limebeck.revealkt.dsl.slides.*
        title = "P0 & <test>"
        configuration {
            theme = RevealKt.Configuration.Theme.Custom("assets/theme.css")
            slideNumber = RevealKt.Configuration.SlideNumber.Custom("c/t")
            autoSlide = -1.0
        }
        slides {
            regularSlide { +title { "First & <safe>" }; +img(src = "pixel.svg") }
            regularSlide { +title { "Second" } }
        }
    """.trimIndent()

    @Test
    fun `packaged CLI exports paths errors browser live reload and PDF`() {
        val dir = Files.createTempDirectory("reveal p0 ")
        cli(dir, "init", "Demo", "--dirname", "presentation space")
        val project = dir.resolve("presentation space")
        assertTrue(project.resolve("build.gradle.kts").exists())
        cli(project, "bundle", "presentation/Demo.reveal.kts", "--output-dir", "template out")
        val script = project.resolve("deck.reveal.kts")
        script.writeText(fixture)
        val assets = project.resolve("assets").createDirectories()
        assets.resolve("theme.css").writeText(".reveal-viewport { background-color: rgb(12, 34, 56); }")
        assets.resolve("pixel.svg").writeText("""<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"><rect width="20" height="20" fill="red"/></svg>""")
        val output = project.resolve("out space")
        cli(project, "bundle", script.name, "--output-dir", output.toString())
        assertTrue(output.resolve("revealkt.js").fileSize() > 1000)
        assertTrue(output.resolve("index.html").readText().contains("First &amp; &lt;safe&gt;"))
        output.resolve("personal.txt").writeText("keep")
        assets.resolve("old.txt").writeText("old")
        cli(dir, "bundle", "presentation space/deck.reveal.kts", "--output-dir", output.toString())
        assets.resolve("old.txt").deleteExisting()
        assets.resolve("theme.css").writeText(".reveal-viewport { background-color: rgb(23, 45, 67); }")
        cli(project, "bundle", script.toString(), "--output-dir", output.toString())
        assertEquals("keep", output.resolve("personal.txt").readText())
        assertEquals("old", output.resolve("assets/old.txt").readText())
        assertEquals(assets.resolve("theme.css").readText(), output.resolve("assets/theme.css").readText())

        Playwright.create(Playwright.CreateOptions().setEnv(mapOf("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD" to "1"))).use { pw ->
            pw.chromium().launch().use { browser ->
                val page = browser.newPage()
                page.onPageError { error("Browser JavaScript error: $it") }
                page.navigate(project.resolve("template out/index.html").toUri().toString())
                page.waitForFunction("() => window.revealKtReady === true")
                assertEquals(false, page.evaluate("() => window.revealKtDeck.getConfig().slideNumber"))
                assertEquals("rgb(25, 25, 25)", page.evaluate("() => getComputedStyle(document.body).backgroundColor"))
                page.navigate(output.resolve("index.html").toUri().toString())
                page.waitForFunction("() => window.revealKtReady === true")
                assertEquals("c/t", page.evaluate("() => window.revealKtDeck.getConfig().slideNumber"))
                assertEquals(false, page.evaluate("() => window.revealKtDeck.getConfig().autoSlide"))
                assertEquals("rgb(23, 45, 67)", page.evaluate("() => getComputedStyle(document.body).backgroundColor"))
                assertEquals(2, page.locator(".slides > section").count())
                assertEquals(true, page.evaluate("() => document.images[0].naturalWidth > 0"))
                val port = ServerSocket(0).use { it.localPort }
                val (server, log) = start(project, "run", script.name, "--host", "127.0.0.1", "--port", "$port")
                try {
                    val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(90)
                    while (true) {
                        try { URI("http://127.0.0.1:$port/").toURL().openStream().use { it.read() }; break }
                        catch (e: Exception) {
                            check(server.isAlive && System.nanoTime() < deadline) { log.readText() }
                            Thread.sleep(100)
                        }
                    }
                    page.navigate("http://127.0.0.1:$port/")
                    page.waitForFunction("() => window.revealKtReady === true && source.readyState === 1")
                    script.writeText(fixture.replace("Second", "Updated"))
                    page.waitForFunction("() => document.body.textContent.includes('Updated')")
                    page.waitForFunction("() => window.revealKtReady === true && source.readyState === 1")
                    page.evaluate("() => { window.revealKtDeck.slide(1); window.lastGoodMarker = true; }")
                    for (invalid in listOf("unknownSymbolForDiagnostic", fixture + "\nerror(\"live execution failed\")")) {
                        script.writeText(invalid)
                        page.waitForFunction("() => document.getElementById('revealkt-error') !== null")
                        assertTrue(page.locator("#revealkt-error").textContent().contains("deck.reveal.kts"))
                        assertTrue(page.locator(".slides").textContent().contains("Updated"))
                        assertEquals(true, page.evaluate("() => window.lastGoodMarker"))
                        assertEquals(1, page.evaluate("() => window.revealKtDeck.getIndices().h"))
                        val newTab = browser.newPage()
                        try {
                            newTab.navigate("http://127.0.0.1:$port/")
                            newTab.waitForFunction("() => document.getElementById('revealkt-error') !== null")
                            assertTrue(newTab.locator(".slides").textContent().contains("Updated"))
                        } finally {
                            newTab.close()
                        }
                        script.writeText(fixture.replace("Second", "Updated"))
                        page.waitForFunction("() => !document.getElementById('revealkt-error') && window.revealKtReady === true")
                        page.waitForFunction("() => source.readyState === 1")
                        page.evaluate("() => { window.revealKtDeck.slide(1); window.lastGoodMarker = true; }")
                    }
                    val nested = assets.resolve("new/nested").createDirectories()
                    nested.resolve("live.css").writeText("first")
                    page.waitForFunction("() => window.revealKtReady === true && source.readyState === 1")
                    // A marker disappears only if an asset event actually reloads the page.
                    page.evaluate("() => window.assetMarker = true")
                    assets.resolve("theme.css").writeText(".reveal-viewport { background-color: rgb(34, 56, 78); }")
                    page.waitForFunction("() => !window.assetMarker && window.revealKtReady === true")
                    assertEquals("rgb(34, 56, 78)", page.evaluate("() => getComputedStyle(document.body).backgroundColor"))
                    page.waitForFunction("() => source.readyState === 1")
                    page.evaluate("() => window.assetMarker = true")
                    nested.resolve("live.css").writeText("changed")
                    page.waitForFunction("() => !window.assetMarker && window.revealKtReady === true")
                } finally {
                    server.destroy()
                    if (!server.waitFor(10, TimeUnit.SECONDS)) server.destroyForcibly()
                }
            }
        }
        val pdf = project.resolve("slides.pdf")
        val pdfPort = ServerSocket(0).use { it.localPort }
        cli(project, "pdf", script.name, "--port", "$pdfPort", "-o", pdf.toString())
        Loader.loadPDF(pdf.toFile()).use { assertEquals(2, it.numberOfPages) }
        ServerSocket(pdfPort).close() // Export released its listening socket.
        assets.resolve("pixel.svg").deleteExisting()
        val missingResourcePdf = project.resolve("missing-resource.pdf")
        val resourceError = cli(project, "pdf", script.name, "--port", "$pdfPort", "-o", missingResourcePdf.toString(), success = false)
        assertTrue(resourceError.contains("pixel.svg"), resourceError)
        assertFalse(missingResourcePdf.exists())
        ServerSocket(pdfPort).close()

        for (invalid in listOf("this is not Kotlin", fixture + "\nerror(\"runtime failure\")")) {
            script.writeText(invalid)
            val previous = output.resolve("index.html").readText()
            val diagnostic = cli(project, "bundle", script.name, "--output-dir", output.toString(), success = false)
            assertTrue(diagnostic.contains("deck.reveal.kts"))
            assertEquals(previous, output.resolve("index.html").readText())
            val failedPdf = project.resolve("failed.pdf")
            cli(project, "pdf", script.name, "--port", "$pdfPort", "-o", failedPdf.toString(), success = false)
            assertFalse(failedPdf.exists())
        }
    }
}
