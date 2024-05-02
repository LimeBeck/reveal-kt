package dev.limebeck.revealkt.dsl

import arrow.optics.Lens
import dev.limebeck.revealkt.core.*
import dev.limebeck.revealkt.core.RevealKt.Configuration
import dev.limebeck.revealkt.elements.slides.MarkdownSlide
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RevealKtBuilder(
    var title: String = "RevealKt"
) {
    class SlidesBuilder : SlidesHolder {
        override val slides = mutableListOf<Slide>()
    }

    class MetaBuilder {
        internal var meta = RevealKt.defaultMeta

        var description: String?
            get() = meta.description
            set(value) {
                meta = meta.copy(description = value)
            }

        var language: String
            get() = meta.language
            set(value) {
                meta = meta.copy(language = value)
            }
    }

    class ConfigurationBuilder {
        internal var configuration = RevealKt.defaultConfiguration

        private fun <T> lens(prop: Lens<Configuration, T>) =
            object : ReadWriteProperty<ConfigurationBuilder, T> {
                override fun getValue(thisRef: ConfigurationBuilder, property: KProperty<*>): T {
                    return prop.get(configuration)
                }

                override fun setValue(thisRef: ConfigurationBuilder, property: KProperty<*>, value: T) {
                    configuration = prop.set(configuration, value)
                }
            }

        var controls by lens(Configuration.controls)
        var controlsTutorial by lens(Configuration.controlsTutorial)
        var controlsLayout by lens(Configuration.controlsLayout)
        var controlsBackArrows by lens(Configuration.controlsBackArrows)
        var navigationMode by lens(Configuration.navigationMode)
        var shuffle by lens(Configuration.shuffle)
        var fragments by lens(Configuration.fragments)
        var fragmentInURL by lens(Configuration.fragmentInURL)
        var help by lens(Configuration.help)
        var pause by lens(Configuration.pause)
        var autoSlide by lens(Configuration.autoSlide)
        var autoSlideStoppable by lens(Configuration.autoSlideStoppable)
        var defaultTiming by lens(Configuration.defaultTiming)
        var mouseWheel by lens(Configuration.mouseWheel)
        var previewLinks by lens(Configuration.previewLinks)
        var postMessage by lens(Configuration.postMessage)
        var postMessageEvents by lens(Configuration.postMessageEvents)
        var pdfSeparateFragments by lens(Configuration.pdfSeparateFragments)

        var slideNumber by lens(Configuration.appearance.slideNumber)
        var showSlideNumber by lens(Configuration.appearance.showSlideNumber)
        var additionalCssStyle by lens(Configuration.appearance.additionalCssStyle)
        var view by lens(Configuration.appearance.view)
        var theme by lens(Configuration.appearance.theme)
        var center by lens(Configuration.appearance.center)
        var touch by lens(Configuration.appearance.touch)
        var progress by lens(Configuration.appearance.progress)
        var showNotes by lens(Configuration.appearance.showNotes)
        var disableLayout by lens(Configuration.appearance.disableLayout)
        var rtl by lens(Configuration.appearance.rtl)
        var overview by lens(Configuration.appearance.overview)

        var keyboard by lens(Configuration.behavior.keyboard)
        var loop by lens(Configuration.behavior.loop)
        var hashOneBasedIndex by lens(Configuration.behavior.hashOneBasedIndex)
        var hash by lens(Configuration.behavior.hash)
        var respondToHashChanges by lens(Configuration.behavior.respondToHashChanges)
        var jumpToSlide by lens(Configuration.behavior.jumpToSlide)
        var history by lens(Configuration.behavior.history)
        var focusBodyOnPageVisibilityChange by lens(Configuration.behavior.focusBodyOnPageVisibilityChange)
        var hideInactiveCursor by lens(Configuration.behavior.hideInactiveCursor)

        var autoAnimate by lens(Configuration.animation.autoAnimate)
        var autoAnimateEasing by lens(Configuration.animation.autoAnimateEasing)
        var autoAnimateDuration by lens(Configuration.animation.autoAnimateDuration)
        var autoAnimateUnmatched by lens(Configuration.animation.autoAnimateUnmatched)
        var transition by lens(Configuration.animation.transition)
        var transitionSpeed by lens(Configuration.animation.transitionSpeed)
        var backgroundTransition by lens(Configuration.animation.backgroundTransition)
    }

    private val slidesBuilder = SlidesBuilder()
    private val configurationBuilder = ConfigurationBuilder()
    private val metaBuilder = MetaBuilder()

    fun build(): RevealKt {
        return RevealKt(
            title = title,
            slides = slidesBuilder.slides,
            configuration = configurationBuilder.configuration,
            meta = metaBuilder.meta
        )
    }

    fun slides(block: SlidesBuilder.() -> Unit) {
        slidesBuilder.block()
    }

    fun configuration(block: ConfigurationBuilder.() -> Unit) {
        configurationBuilder.block()
    }

    fun meta(block: MetaBuilder.() -> Unit) {
        metaBuilder.block()
    }
}

fun revealKt(title: String = "RevealKt", init: RevealKtBuilder.() -> Unit): RevealKtBuilder =
    RevealKtBuilder(title).apply {
        init()
    }


fun SlidesHolder.markdownSlide(
    id: ID = UuidGenerator.generateId(),
    block: () -> String
) {
    slides.add(MarkdownSlide(id, block()))
}

@DslMarker
annotation class RevealKtMarker
