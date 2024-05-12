import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.core.RevealKt.Configuration
import dev.limebeck.revealkt.core.controls
import dev.limebeck.revealkt.utils.asLensProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class ProviderTest {
    @Test
    fun test() {
        val configuration = RevealKt.defaultConfiguration.asLensProvider()
        var controls by configuration(Configuration.controls)
        controls = false
        assertEquals(false, configuration.get().controls)
    }
}
