package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.elements.slides.Slide

@RevealKtMarker
interface SlidesHolder {
    val slides: MutableList<Slide>
}