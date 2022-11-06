package dev.limebeck.revealkt.dsl.slides

import dev.limebeck.revealkt.dsl.SlidesHolder
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.elements.slides.VerticalSlide
import dev.limebeck.revealkt.utils.UuidGenerator

class VerticalSlideBuilder : SlidesHolder {
    var autoanimate: Boolean = false
    var id = UuidGenerator.generateId()

    override val slides = mutableListOf<Slide>()

    fun build(): VerticalSlide {
        return VerticalSlide(
            id = id,
            autoanimate = autoanimate,
            slides = slides
        )
    }
}

fun SlidesHolder.verticalSlide(
    block: VerticalSlideBuilder .() -> Unit
) {
    slides.add(VerticalSlideBuilder().apply(block).build())
}