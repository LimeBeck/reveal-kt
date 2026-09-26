import dev.limebeck.revealkt.core.elements.QrCode
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class QrCodeTest {
    @Test
    fun `qr code has a default id and a well-formed data uri`() {
        val first = QrCode(value = "https://example.com")
        assertNotEquals(first.id, QrCode(value = "https://example.com").id)
        assertTrue(first.renderToString().contains("src=\"data:image/png;base64,iVBOR"))
    }
}
