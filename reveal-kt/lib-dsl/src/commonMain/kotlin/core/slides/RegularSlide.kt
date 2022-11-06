package dev.limebeck.revealkt.elements.slides

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.section

data class RegularSlide(
    override val id: ID = UuidGenerator.generateId(),
    val autoanimate: Boolean,
    val elements: List<RevealKtElement>
) : Slide {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        section {
            if (autoanimate) attributes["data-auto-animate"] = ""
            for (element in elements) {
                element.render(this)
            }
        }
    }
}