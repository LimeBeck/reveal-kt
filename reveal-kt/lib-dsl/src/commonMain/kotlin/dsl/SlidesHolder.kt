package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.elements.slides.Slide

interface SlidesHolder {
    val slides: MutableList<Slide>
}