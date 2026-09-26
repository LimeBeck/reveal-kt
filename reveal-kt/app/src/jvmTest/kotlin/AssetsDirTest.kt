import dev.limebeck.application.server.renderLoadResult
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import java.nio.file.Files
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AssetsDirTest {
    @Test
    fun `loadAsset reads from the assets directory the server serves`() {
        val root = Files.createTempDirectory("assets dir ")
        val script = root.resolve("scripts").createDirectories().resolve("deck.reveal.kts")
        script.writeText("slides { regularSlide { +title { loadAsset(\"greeting.txt\").decodeToString() } } }")
        val assets = root.resolve("resources/assets").createDirectories()
        assets.resolve("greeting.txt").writeText("Hello from base path")

        val result = RevealKtScriptLoader().loadScript(script.toFile(), assets.toFile())
        assertIs<RevealKtScriptLoader.LoadResult.Success>(result)
        assertTrue(renderLoadResult(result).contains("Hello from base path"))
    }
}
