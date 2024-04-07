package dev.limebeck.revealkt.dsl.slides

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.dsl.ElementsHolder
import dev.limebeck.revealkt.dsl.RevealKtMarker
import dev.limebeck.revealkt.dsl.SlidesHolder
import dev.limebeck.revealkt.elements.slides.RegularSlide
import dev.limebeck.revealkt.utils.UuidGenerator

@RevealKtMarker
class RegularSlideBuilder : ElementsHolder {
    var autoanimate: Boolean = true
    var id = UuidGenerator.generateId()

    override val elements = mutableListOf<RevealKtElement>()
//
//    operator fun RevealKtElement.unaryPlus() {
//        elements.add(this)
//    }

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
