import dev.limebeck.application.server.renderLoadResult
import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import dev.limebeck.revealkt.server.ConfigurationDto
import dev.limebeck.revealkt.server.configurationJsonMapper
import kotlinx.serialization.encodeToString
import kotlin.test.*

class RenderingTest {
    @Test
    fun `HTML preserves structure attributes and escapes text and inline configuration`() {
        val builder = revealKt("Title <&>") {
            configuration { additionalCssStyle = "</script><script>bad()</script>" }
            slides {
                verticalSlide {
                    regularSlide {
                        autoanimate = true
                        +title { "Heading <&>" }
                        +img(src = "a&b.png")
                        +note { "Notes <&>" }
                    }
                }
            }
        }
        val html = renderLoadResult(RevealKtScriptLoader.LoadResult.Success(builder))
        assertTrue(html.startsWith("<!DOCTYPE html>"))
        assertTrue(html.contains("<title>Title &lt;&amp;&gt;</title>"))
        assertTrue(html.contains("class=\"reveal\""))
        assertTrue(html.contains("class=\"slides\""))
        assertEquals(2, Regex("<section[ >]").findAll(html).count())
        assertTrue(html.contains("data-auto-animate"))
        assertTrue(html.contains("Heading &lt;&amp;&gt;"))
        assertTrue(html.contains("a&amp;b.png"))
        assertTrue(html.contains("Notes &lt;&amp;&gt;"))
        assertFalse(html.contains("</script><script>bad()"))
        assertTrue(html.contains("\\u003c/script>"))
    }

    @Test
    fun `custom configuration survives DSL DTO and JSON roundtrip`() {
        val built = revealKt("test") {
            configuration {
                controls = false
                progress = false
                theme = RevealKt.Configuration.Theme.Custom("assets/theme with spaces.css")
                slideNumber = RevealKt.Configuration.SlideNumber.Custom("h.v")
                autoSlide = -1.0
            }
        }.build()
        val dto = ConfigurationDto(built.configuration)
        val decoded = configurationJsonMapper.decodeFromString<ConfigurationDto>(configurationJsonMapper.encodeToString(dto))
        assertEquals(dto, decoded)
        assertFalse(decoded.controls)
        assertFalse(decoded.progress)
        assertEquals(ConfigurationDto.Theme.Custom("assets/theme with spaces.css"), decoded.theme)
        assertEquals(ConfigurationDto.SlideNumber.Custom("h.v"), decoded.slideNumber)
        assertEquals(-1.0, decoded.autoSlide)
        for (theme in RevealKt.Configuration.Theme.Predefined.entries) {
            assertEquals(theme.name, ConfigurationDto.Theme.Predefined.of(theme).name)
        }
        assertEquals(ConfigurationDto.SlideNumber.Enable, ConfigurationDto.SlideNumber.of(RevealKt.Configuration.SlideNumber.Enable))
        assertEquals(ConfigurationDto.SlideNumber.Disable, ConfigurationDto.SlideNumber.of(RevealKt.Configuration.SlideNumber.Disable))
    }
}
