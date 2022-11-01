package dev.limebeck.revealkt.elements.slides

import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.section

data class VerticalSlide(
    override val id: ID = UuidGenerator.generateId(),
    val autoanimate: Boolean = false,
    val slides: List<Slide>,
) : Slide {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        section {
            if (autoanimate) attributes["data-auto-animate"] = ""
            for (slide in slides) {
                slide.render(this)
            }
        }
    }
}