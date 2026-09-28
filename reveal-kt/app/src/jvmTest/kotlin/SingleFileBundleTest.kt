import com.github.ajalt.clikt.testing.test
import com.microsoft.playwright.Playwright
import dev.limebeck.application.bundle.bundleSingleFile
import dev.limebeck.application.commands.BundleToStatic
import java.nio.file.Files
import java.util.Base64
import kotlin.io.path.*
import kotlin.test.*

class SingleFileBundleTest {
    private fun decode(dataUrl: String) = Base64.getDecoder().decode(dataUrl.substringAfter("base64,")).decodeToString()

    @Test
    fun `runtime assets and stylesheet urls are embedded`() {
        val assets = Files.createTempDirectory("single assets ").resolve("assets").createDirectories()
        assets.resolve("a b.svg").writeText("<svg/>")
        assets.resolve("theme.css").writeText(
            ".x { background: url('bg.png') } .y { background: url(https://example.com/r.png) } .z { background: url(gone.png) }"
        )
        assets.resolve("bg.png").writeBytes(byteArrayOf(1, 2, 3))
        val html = """
            <html><body>
            <section data-background-image="assets/bg.png"><img src="assets/a b.svg"><img src="https://example.com/x.png"></section>
            <script>const configurationJson = {"theme":["Custom",{"cssLink":"assets/theme.css"}]}</script>
            <script src="revealkt.js" type="text/javascript"></script>
            </body></html>
        """.trimIndent()
        val result = bundleSingleFile(html, "console.log('\$&</script>')".encodeToByteArray(), assets)

        assertFalse(result.html.contains("revealkt.js"))
        val script = Regex("""<script src="(data:text/javascript;base64,[^"]+)"></script>""").find(result.html)!!.groupValues[1]
        assertEquals("console.log('\$&</script>')", decode(script))
        assertTrue(result.html.contains("data-background-image=\"data:image/png;base64,AQID\""))
        val image = Regex("""<img src="(data:image/svg\+xml;base64,[^"]+)">""").find(result.html)!!.groupValues[1]
        assertEquals("<svg/>", decode(image))
        assertTrue(result.html.contains("<img src=\"https://example.com/x.png\">"))
        val css = decode(Regex(""""cssLink":"(data:text/css;base64,[^"]+)"""").find(result.html)!!.groupValues[1])
        assertTrue(css.contains("url(\"data:image/png;base64,AQID\")"), css)
        assertTrue(css.contains("url(https://example.com/r.png)"))
        assertEquals(listOf("gone.png"), result.missing)
    }

    @Test
    fun `bundle --single-file opens offline from a lone html file`() {
        val project = Files.createTempDirectory("single project ")
        val assets = project.resolve("assets").createDirectories()
        assets.resolve("theme.css").writeText(".reveal-viewport { background: url(tile.svg); background-color: rgb(12, 34, 56); }")
        assets.resolve("tile.svg").writeText("""<svg xmlns="http://www.w3.org/2000/svg" width="4" height="4"/>""")
        assets.resolve("pixel.svg").writeText("""<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"><rect width="20" height="20" fill="red"/></svg>""")
        val script = project.resolve("deck.reveal.kts")
        script.writeText(
            """
            import dev.limebeck.revealkt.core.RevealKt
            configuration { theme = RevealKt.Configuration.Theme.Custom("assets/theme.css") }
            slides {
                regularSlide {
                    background { image = "pixel.svg" }
                    +title("Offline")
                    +img("pixel.svg")
                }
            }
            """.trimIndent()
        )
        val out = project.resolve("out")
        val result = BundleToStatic().test(listOf("--single-file", "--output-dir", out.toString(), script.toString()))
        assertEquals(0, result.statusCode, result.output)
        assertEquals(listOf("deck.html"), out.listDirectoryEntries().map { it.name })

        // Nothing else travels with the file.
        val lone = Files.createTempDirectory("single lone ").resolve("deck.html")
        out.resolve("deck.html").copyTo(lone)
        Playwright.create(Playwright.CreateOptions().setEnv(mapOf("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD" to "1"))).use { pw ->
            pw.chromium().launch().use { browser ->
                val page = browser.newPage()
                val requests = mutableListOf<String>()
                page.onRequest { if (!it.url().startsWith("data:")) requests += it.url() }
                page.onPageError { error("Browser JavaScript error: $it") }
                page.navigate(lone.toUri().toString())
                page.waitForFunction("() => window.revealKtReady === true")
                page.waitForFunction("() => Array.from(document.images).every(img => img.complete)")
                assertEquals(true, page.evaluate("() => document.images[0].naturalWidth === 20"))
                page.waitForFunction("() => getComputedStyle(document.body).backgroundColor === 'rgb(12, 34, 56)'")
                assertEquals(listOf(lone.toUri().toString()), requests.filterNot { it.startsWith("https://fonts.") })
            }
        }
    }
}
