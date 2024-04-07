package dev.limebeck.revealkt.dsl.slides

import dev.limebeck.revealkt.dsl.RevealKtMarker
import dev.limebeck.revealkt.dsl.SlidesHolder
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.elements.slides.VerticalSlide
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@RevealKtMarker
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

@OptIn(ExperimentalContracts::class)
fun SlidesHolder.verticalSlide(
    block: VerticalSlideBuilder .() -> Unit
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    slides.add(VerticalSlideBuilder().apply(block).build())
}