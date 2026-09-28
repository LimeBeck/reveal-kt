package dev.limebeck.revealkt.elements.slides

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import dev.limebeck.revealkt.utils.conditionalAttribute
import kotlinx.html.HtmlBlockTag
import kotlinx.html.section

data class RegularSlide(
    override val id: ID = UuidGenerator.generateId(),
    val autoanimate: Boolean,
    val elements: List<RevealKtElement>,
    val options: SlideOptions = SlideOptions(),
) : Slide {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        section {
            conditionalAttribute("data-auto-animate", ::autoanimate)
            renderSlideOptions(options)
            for (element in elements) {
                element.render(this)
            }
        }
    }
}
