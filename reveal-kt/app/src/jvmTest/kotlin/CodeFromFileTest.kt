import dev.limebeck.application.server.Config
import dev.limebeck.application.server.ServerConfig
import dev.limebeck.application.server.renderLoadResult
import dev.limebeck.application.server.runServer
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import dev.limebeck.revealkt.scripts.formatDiagnostics
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.*

class CodeFromFileTest {
    private fun project(): Pair<Path, Path> {
        val root = Files.createTempDirectory("code from file ")
        root.resolve("src").createDirectories().resolve("Service.kt").writeText(
            """
            class Service {
                // region fetch
                fun fetch() = "v1"
                // endregion
            }
            """.trimIndent()
        )
        val script = root.resolve("slides").createDirectories().resolve("deck.reveal.kts")
        script.writeText("""slides { regularSlide { +codeFromFile("../src/Service.kt", region = "fetch", lineNumbers = "1") } }""")
        return root to script
    }

    @Test
    fun `scripts show code from files relative to the script`() {
        val (root, script) = project()
        val result = RevealKtScriptLoader().loadScript(script.toFile())
        assertIs<RevealKtScriptLoader.LoadResult.Success>(result)
        val html = renderLoadResult(result)
        assertTrue(html.contains("<pre class=\"kotlin\""), html)
        assertTrue(html.contains("data-line-numbers=\"1\""))
        assertTrue(html.contains("fun fetch() = \"v1\""))
        assertFalse(html.contains("region"))
        assertEquals(setOf(root.resolve("src/Service.kt").toRealPath().toFile()), result.dependencies.map { it.canonicalFile }.toSet())
    }

    @Test
    fun `a missing region is reported as a script error`() {
        val (_, script) = project()
        script.writeText("""slides { regularSlide { +codeFromFile("../src/Service.kt", region = "absent") } }""")
        val result = RevealKtScriptLoader().loadScript(script.toFile())
        assertIs<RevealKtScriptLoader.LoadResult.Error>(result)
        assertTrue(result.formatDiagnostics(script.toFile()).contains("Region 'absent' is not found"))
    }

    @Test
    fun `preview reloads when a shown source file changes outside the script directory`() {
        val (root, script) = project()
        runServer(
            Config(ServerConfig(host = "127.0.0.1", port = 0), script.parent.toString(), script.toFile()),
            blocking = false,
        ).use { server ->
            fun page() = URI("${server.url}/").toURL().readText()
            assertTrue(page().contains("\"v1\""))
            root.resolve("src/Service.kt").writeText(
                """
                class Service {
                    // region fetch
                    fun fetch() = "v2"
                    // endregion
                }
                """.trimIndent()
            )
            val deadline = System.nanoTime() + 60_000_000_000
            while (!page().contains("\"v2\"")) {
                check(System.nanoTime() < deadline) { "Page was not re-rendered after the source file changed" }
                Thread.sleep(200)
            }
        }
    }
}
