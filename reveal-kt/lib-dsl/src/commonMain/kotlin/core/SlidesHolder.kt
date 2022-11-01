package dev.limebeck.revealkt.elements

import dev.limebeck.revealkt.elements.slides.Slide

interface SlidesHolder {
    val slides: MutableList<Slide>
}