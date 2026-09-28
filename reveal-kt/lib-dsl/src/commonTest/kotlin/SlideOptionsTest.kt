import dev.limebeck.revealkt.core.RevealKt.Configuration.Transition
import dev.limebeck.revealkt.core.RevealKt.Configuration.TransitionSpeed
import dev.limebeck.revealkt.core.elements.ListItem
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.regularSlide
import dev.limebeck.revealkt.elements.slides.Slide
import kotlin.test.*

class SlideOptionsTest {
    private fun renderSingleSlide(block: RevealKtBuilder.SlidesBuilder.() -> Unit): String {
        val slides: List<Slide> = revealKt { slides(block) }.build().slides
        return slides.single().renderToString()
    }

    @Test
    fun `slides render backgrounds transitions and visibility`() {
        val html = renderSingleSlide {
            regularSlide {
                transition = Transition.ZOOM
                transitionSpeed = TransitionSpeed.FAST
                backgroundTransition = Transition.FADE
                hidden = true
                background {
                    image = "hero.png"
                    size = "cover"
                    opacity = 0.5
                    color = "#101820"
                    video = "https://example.com/loop.mp4"
                    videoLoop = true
                }
                +title("Backgrounds")
            }
        }
        assertTrue(html.contains("data-background-image=\"assets/hero.png\""), html)
        assertTrue(html.contains("data-background-size=\"cover\""))
        assertTrue(html.contains("data-background-opacity=\"0.5\""))
        assertTrue(html.contains("data-background-color=\"#101820\""))
        assertTrue(html.contains("data-background-video=\"https://example.com/loop.mp4\""))
        assertTrue(html.contains("data-background-video-loop=\"\""))
        assertTrue(html.contains("data-transition=\"zoom\""))
        assertTrue(html.contains("data-transition-speed=\"fast\""))
        assertTrue(html.contains("data-background-transition=\"fade\""))
        assertTrue(html.contains("data-visibility=\"hidden\""))
    }

    @Test
    fun `slides without options keep plain sections`() {
        val html = renderSingleSlide { regularSlide { +title("Plain") } }
        assertFalse(html.contains("data-background"), html)
        assertFalse(html.contains("data-transition"), html)
        assertFalse(html.contains("data-visibility"), html)
    }

    @Test
    fun `fragments wrap one element or a group`() {
        val html = renderSingleSlide {
            regularSlide {
                +title("Always visible")
                +regular("First").asFragment(ListItem.Effect.FADE_UP, index = 2)
                +fragment(index = 1) {
                    +regular("Second")
                    +regular("Third")
                }
            }
        }
        assertTrue(Regex("<div[^>]*class=\"fragment fade-up\"[^>]*data-fragment-index=\"2\"[^>]*><span[^>]*>First</span></div>").containsMatchIn(html), html)
        assertTrue(Regex("<div[^>]*class=\"fragment\"[^>]*data-fragment-index=\"1\"[^>]*><span[^>]*>Second</span><span[^>]*>Third</span></div>").containsMatchIn(html), html)
        assertFalse(Regex("<h1[^>]*fragment").containsMatchIn(html))
    }

    @Test
    fun `images accept urls as well as asset paths`() {
        assertTrue(img("photo.png").renderToString().contains("src=\"assets/photo.png\""))
        assertTrue(img("https://example.com/a.png").renderToString().contains("src=\"https://example.com/a.png\""))
        assertTrue(img("data:image/png;base64,AA==").renderToString().contains("src=\"data:image/png;base64,AA==\""))
    }
}
