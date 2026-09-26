import com.github.ajalt.clikt.testing.test
import dev.limebeck.application.commands.RenderPdf
import dev.limebeck.application.filesWatcher.UpdatedFile
import dev.limebeck.application.server.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.time.Instant
import kotlin.io.path.writeText
import kotlin.test.*

class ServerTest {
    @Test
    fun `unexpected errors are reported as escaped 500 pages`() = testApplication {
        application {
            installErrorPages()
            routing { get("/boom") { throw IllegalStateException("Type mismatch: List<String> & <b>") } }
        }
        val response = client.get("/boom")
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Type mismatch: List&lt;String&gt; &amp; &lt;b&gt;"), body)
        assertFalse(body.contains("<b>"))
    }

    @Test
    fun `an unrelated change cannot swallow a pending script change`() = runBlocking {
        val root = Path.of("/project").toAbsolutePath()
        val requests = ReloadRequests(root.resolve("deck.reveal.kts"), root.resolve("assets"))
        fun event(path: Path) = listOf(UpdatedFile(path.toString(), ENTRY_MODIFY, Instant.now()))

        requests.offer(event(root.resolve("notes.txt")))
        assertNull(withTimeoutOrNull(200) { requests.signals.first() })

        requests.offer(event(root.resolve("deck.reveal.kts")))
        requests.offer(event(root.resolve(".deck.reveal.kts.swp")))
        withTimeout(1000) { requests.signals.first() }

        requests.offer(event(root.resolve("assets/nested/theme.css")))
        withTimeout(1000) { requests.signals.first() }
    }

    @Test
    fun `preview listens on localhost and wildcard hosts open as localhost`() {
        assertEquals("localhost", ServerConfig().host)
        assertEquals("localhost", browserHost("0.0.0.0"))
        assertEquals("localhost", browserHost("::"))
        assertEquals("127.0.0.1", browserHost("127.0.0.1"))
        assertEquals("[::1]", browserHost("::1"))
    }

    @Test
    fun `port 0 lets several servers start and reports the bound port`() {
        val dir = Files.createTempDirectory("server port ")
        val script = dir.resolve("deck.reveal.kts")
        script.writeText("slides { regularSlide { +title { \"Port\" } } }")
        fun start() = runServer(
            Config(ServerConfig(host = "127.0.0.1", port = 0), dir.toString(), script.toFile()),
            blocking = false,
            liveReload = false,
        )
        start().use { first ->
            start().use { second ->
                assertNotEquals(first.url, second.url)
                for (url in listOf(first.url, second.url)) {
                    assertTrue(url.matches(Regex("http://127\\.0\\.0\\.1:\\d+")), url)
                    assertFalse(url.endsWith(":0"))
                    val connection = URI("$url/").toURL().openConnection() as HttpURLConnection
                    assertEquals(200, connection.responseCode)
                    assertTrue(connection.inputStream.readAllBytes().decodeToString().contains("Port"))
                }
            }
        }
    }

    @Test
    fun `pdf keeps -h for help`() {
        val result = RenderPdf().test("-h")
        assertTrue(result.output.contains("Usage"), result.output)
        assertTrue(result.output.contains("--host"), result.output)
    }
}
