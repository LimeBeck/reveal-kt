package dev.limebeck.revealkt.dsl.slides

import dev.limebeck.revealkt.utils.UuidGenerator
import dev.limebeck.revealkt.elements.RevealKtElement
import dev.limebeck.revealkt.elements.SlidesHolder
import dev.limebeck.revealkt.elements.slides.RegularSlide

class RegularSlideBuilder {
    var autoanimate: Boolean = false
    var id = UuidGenerator.generateId()

    internal val elements = mutableListOf<RevealKtElement>()

    operator fun RevealKtElement.unaryPlus() {
        elements.add(this)
    }

    fun build(): RegularSlide {
        return RegularSlide(
            id = id,
            autoanimate = autoanimate,
            elements = elements
        )
    }
}

fun SlidesHolder.regularSlide(
    block: RegularSlideBuilder.() -> Unit
) {
    slides.add(RegularSlideBuilder().apply(block).build())
}

fun SlidesHolder.slide(
    block: RegularSlideBuilder.() -> Unit
) {
    slides.add(RegularSlideBuilder().apply(block).build())
}
