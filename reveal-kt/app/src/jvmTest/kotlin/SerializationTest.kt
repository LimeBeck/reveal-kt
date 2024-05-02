import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.server.ConfigurationDto
import dev.limebeck.revealkt.server.configurationJsonMapper
import kotlinx.serialization.encodeToString
import kotlin.test.Test

class SerializationTest {
    @Test
    fun `Serialize configuration`() {
        configurationJsonMapper.encodeToString(ConfigurationDto(RevealKt.Configuration()))
    }
}