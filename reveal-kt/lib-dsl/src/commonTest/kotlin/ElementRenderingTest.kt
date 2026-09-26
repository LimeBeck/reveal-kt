import dev.limebeck.revealkt.core.AttributedElement
import dev.limebeck.revealkt.core.BooleanAttributeDelegate
import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.core.elements.Code
import dev.limebeck.revealkt.core.elements.Title
import dev.limebeck.revealkt.dsl.revealKt
import dev.limebeck.revealkt.utils.ID
import kotlinx.css.Color
import kotlinx.css.color
import kotlinx.html.HtmlBlockTag
import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlin.test.*

fun RevealKtElement.renderToString(): String = createHTML(prettyPrint = false).div { render(this) }

class ElementRenderingTest {
    private class Flagged : AttributedElement(ID("flagged")) {
        var flag by BooleanAttributeDelegate("data-flag")

        override fun HtmlBlockTag.tagBodyProvider(renderAttributes: HtmlBlockTag.() -> Unit) {
            span { renderAttributes() }
        }
    }

    @Test
    fun `boolean attribute delegate reads back the value it writes`() {
        val element = Flagged()
        assertFalse(element.flag)
        element.flag = true
        assertTrue(element.flag)
        assertTrue(element.renderToString().contains("data-flag=\"\""))
        element.flag = false
        assertFalse(element.flag)
        assertFalse(element.renderToString().contains("data-flag"))
    }

    @Test
    fun `additional css does not leak between presentations`() {
        fun build() = revealKt {
            configuration { additionalCss { rule(".leak") { color = Color.red } } }
        }.build().configuration.appearance.additionalCssStyleBuilder.toString()

        val first = build()
        val second = build()
        assertEquals(1, Regex("\\.leak").findAll(first).count())
        assertEquals(first, second)
        assertEquals("", RevealKt.defaultConfiguration.appearance.additionalCssStyleBuilder.toString())
    }

    @Test
    fun `code keeps plain snippets in a script template`() {
        val html = Code(id = ID("c"), code = "val x = listOf<Int>() && true").renderToString()
        assertTrue(html.contains("<script type=\"text/template\">val x = listOf<Int>() && true</script>"))
    }

    @Test
    fun `code containing a script end tag cannot break the page`() {
        val snippet = "<script>alert(1)</SCRIPT><!-- comment -->"
        val html = Code(id = ID("c"), lang = "html", code = snippet).renderToString()
        assertFalse(html.contains("text/template"))
        assertFalse(html.contains("</SCRIPT>"))
        assertTrue(html.contains("&lt;script&gt;alert(1)&lt;/SCRIPT&gt;&lt;!-- comment --&gt;"))
    }

    @Test
    fun `code constructors share the same trim default`() {
        assertEquals(Code(code = "x").trim, Code { "x" }.trim)
    }

    @Test
    fun `elements without extra styles do not render an empty style attribute`() {
        val html = Title(id = ID("t"), title = "Plain").renderToString()
        assertFalse(html.contains("style="), html)
    }
}
