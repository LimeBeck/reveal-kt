import com.sun.net.httpserver.HttpServer
import dev.limebeck.application.pdf.PdfRenderer
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import kotlin.test.*

class PdfReadinessTest {
    @Test
    fun `missing readiness times out with diagnostics`() = runBlocking {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val body = "<html><body>Never ready</body></html>".toByteArray()
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        server.start()
        try {
            val failure = assertFailsWith<IllegalStateException> {
                PdfRenderer().render("http://127.0.0.1:${server.address.port}/", 1000.0)
            }
            assertTrue(failure.message!!.contains("readiness timeout 1000 ms"))
            assertTrue(failure.message!!.contains("Browser diagnostics"))
        } finally {
            server.stop(0)
        }
    }
}
