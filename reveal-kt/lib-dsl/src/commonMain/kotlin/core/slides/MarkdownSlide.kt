package dev.limebeck.revealkt.elements.slides

import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.section
import kotlinx.html.textArea

data class MarkdownSlide(
    override val id: ID = UuidGenerator.generateId(),
    val markdown: String
) : Slide {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        section {
            attributes["data-markdown"] = "true"
            textArea {
                attributes["data-template"] = "true"
                text(markdown)
            }
        }
    }
}