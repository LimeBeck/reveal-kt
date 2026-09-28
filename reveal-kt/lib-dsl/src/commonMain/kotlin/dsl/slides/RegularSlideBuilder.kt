package dev.limebeck.revealkt.dsl.slides

import dev.limebeck.revealkt.core.RevealKt.Configuration.Transition
import dev.limebeck.revealkt.core.RevealKt.Configuration.TransitionSpeed
import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.dsl.ElementsHolder
import dev.limebeck.revealkt.dsl.RevealKtMarker
import dev.limebeck.revealkt.dsl.SlidesHolder
import dev.limebeck.revealkt.elements.slides.RegularSlide
import dev.limebeck.revealkt.elements.slides.SlideBackground
import dev.limebeck.revealkt.elements.slides.SlideOptions
import dev.limebeck.revealkt.utils.UuidGenerator

@RevealKtMarker
class RegularSlideBuilder : ElementsHolder {
    var autoanimate: Boolean = true
    var id = UuidGenerator.generateId()

    /** Overrides the presentation transition for this slide. */
    var transition: Transition? = null
    var transitionSpeed: TransitionSpeed? = null
    var backgroundTransition: Transition? = null

    /** Skips the slide unless `showHiddenSlides` is enabled. */
    var hidden: Boolean = false

    private var background: SlideBackground? = null

    override val elements = mutableListOf<RevealKtElement>()

    fun background(block: SlideBackgroundBuilder.() -> Unit) {
        background = SlideBackgroundBuilder().apply(block).build()
    }

    fun build(): RegularSlide {
        return RegularSlide(
            id = id,
            autoanimate = autoanimate,
            elements = elements,
            options = SlideOptions(background, transition, transitionSpeed, backgroundTransition, hidden),
        )
    }
}

@RevealKtMarker
class SlideBackgroundBuilder {
    var color: String? = null

    /** Relative paths point into `assets/`; URLs are used as they are. */
    var image: String? = null
    var size: String? = null
    var position: String? = null
    var repeat: String? = null
    var opacity: Double? = null

    /** A CSS gradient, for example `"linear-gradient(to bottom, #283b95, #17b2c3)"`. */
    var gradient: String? = null

    /** Relative paths point into `assets/`; URLs are used as they are. */
    var video: String? = null
    var videoLoop: Boolean = false
    var videoMuted: Boolean = false
    var iframe: String? = null
    var interactive: Boolean = false

    fun build() = SlideBackground(
        color, image, size, position, repeat, opacity, gradient, video, videoLoop, videoMuted, iframe, interactive
    )
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
