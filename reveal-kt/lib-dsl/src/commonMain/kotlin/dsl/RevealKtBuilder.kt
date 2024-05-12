package dev.limebeck.revealkt.dsl

import arrow.optics.Lens
import dev.limebeck.revealkt.core.*
import dev.limebeck.revealkt.core.RevealKt.Configuration
import dev.limebeck.revealkt.elements.slides.MarkdownSlide
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.css.CssBuilder
import kotlinx.html.HEAD
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RevealKtBuilder(
    var title: String = "RevealKt"
) {
    private var headModifier: HEAD.() -> Unit = {}

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

        /**
         * Display presentation control arrows
         */
        var controls by lens(Configuration.controls)

        /**
         * Help the user learn the controls by providing hints, for example by
         * bouncing the down arrow when they first encounter a vertical slide
         */
        var controlsTutorial by lens(Configuration.controlsTutorial)

        /**
         * Determines where controls appear, "edges" or "bottom-right"
         */
        var controlsLayout by lens(Configuration.controlsLayout)

        /**
         *   Visibility rule for backwards navigation arrows; "faded", "hidden"
         *   or "visible"
         */
        var controlsBackArrows by lens(Configuration.controlsBackArrows)

        /**
         * 	Changes the behavior of our navigation directions.
         *
         * 	**default**
         * 	Left/right arrow keys step between horizontal slides, up/down
         * 	arrow keys step between vertical slides. Space key steps through
         * 	all slides (both horizontal and vertical).
         *
         * 	**linear**
         * 	Removes the up/down arrows. Left/right arrows step through all
         * 	slides (both horizontal and vertical).
         *
         * 	**grid**
         * 	When this is enabled, stepping left/right from a vertical stack
         * 	to an adjacent vertical stack will land you at the same vertical
         * 	index.
         *
         * 	Consider a deck with six slides ordered in two vertical stacks:
         * 	* 1.1   |   2.1
         * 	* 1.2   |   2.2
         * 	* 1.3   |   2.3
         *
         * 	If you're on slide 1.3 and navigate right, you will normally move
         * 	from 1.3 -> 2.1. If "grid" is used, the same navigation takes you
         * 	from 1.3 -> 2.3.
         */
        var navigationMode by lens(Configuration.navigationMode)

        /**
         * Randomizes the order of slides each time the presentation loads
         */
        var shuffle by lens(Configuration.shuffle)

        /**
         * Turns fragments on and off globally
         */
        var fragments by lens(Configuration.fragments)

        /**
         * Flags whether to include the current fragment in the URL,
         * 	so that reloading brings you to the same fragment position
         */
        var fragmentInURL by lens(Configuration.fragmentInURL)

        /**
         * Flags if we should show a help overlay when the question-mark
         * key is pressed
         */
        var help by lens(Configuration.help)

        /**
         * Flags if it should be possible to pause the presentation (blackout)
         */
        var pause by lens(Configuration.pause)

        /**
         * Controls automatic progression to the next slide
         *  - 0:      Auto-sliding only happens if the data-autoslide HTML attribute
         *            is present on the current slide or fragment
         *  - 1+:     All slides will progress automatically at the given interval
         *  - <0:  No auto-sliding, even if data-autoslide is present
         */
        var autoSlide by lens(Configuration.autoSlide)

        /**
         * Stop auto-sliding after user input
         */
        var autoSlideStoppable by lens(Configuration.autoSlideStoppable)

        /**
         * Specify the average time in seconds that you think you will spend
         * presenting each slide. This is used to show a pacing timer in the
         * speaker view
         */
        var defaultTiming by lens(Configuration.defaultTiming)

        /**
         * Enable slide navigation via mouse wheel
         */
        var mouseWheel by lens(Configuration.mouseWheel)

        /**
         * Opens links in an iframe preview overlay
         * Add `data-preview-link` and `data-preview-link="false"` to customise each link
         * individually
         */
        var previewLinks by lens(Configuration.previewLinks)

        /**
         * Prints each fragment on a separate slide
         */
        var pdfSeparateFragments by lens(Configuration.pdfSeparateFragments)

        /**
         *   Display the page number of the current slide
         *   - true:    Show slide number
         *   - false:   Hide slide number
         *
         *   Can optionally be set as a string that specifies the number formatting:
         *   - "h.v":   Horizontal . vertical slide number (default)
         *   - "h/v":   Horizontal / vertical slide number
         *   - "c":   Flattened slide number
         *   - "c/t":   Flattened slide number / total slides
         *
         *   Alternatively, you can provide a function that returns the slide
         *   number for the current slide. The function should take in a slide
         *   object and return an array with one string [slideNumber] or
         *   three strings [n1,delimiter,n2]. See #formatSlideNumber().
         */
        var slideNumber by lens(Configuration.appearance.slideNumber)

        /**
         *  Can be used to limit the contexts in which the slide number appears
         *  - `ALL`:      Always show the slide number
         *  - `PRINT`:    Only when printing to PDF
         *  - `SPEAKER`:  Only in the speaker view
         */
        var showSlideNumber by lens(Configuration.appearance.showSlideNumber)
        var additionalCssStyle by lens(Configuration.appearance.additionalCssStyle)

        /**
         *  Can be used to initialize reveal.js in one of the following views:
         *  - `VIEW`: Regular view
         * 	- `PRINT`:   Render the presentation so that it can be printed to PDF
         * 	- `SCROLL`:  Show the presentation as a tall scrollable page with scroll
         * 	           triggered animations
         */
        var view by lens(Configuration.appearance.view)
        var theme by lens(Configuration.appearance.theme)

        /**
         * Vertical centering of slides
         */
        var center by lens(Configuration.appearance.center)

        /**
         * Enables touch navigation on devices with touch input
         */
        var touch by lens(Configuration.appearance.touch)

        /**
         * Display a presentation progress bar
         */
        var progress by lens(Configuration.appearance.progress)

        /**
         * Flags if speaker notes should be visible to all viewers
         */
        var showNotes by lens(Configuration.appearance.showNotes)

        /**
         * Flags if slides with data-visibility="hidden" should be kep visible
         */
        var showHiddenSlides by lens(Configuration.appearance.showHiddenSlides)

        /**
         * Disables the default reveal.js slide layout (scaling and centering)
         * so that you can use custom CSS layout
         */
        var disableLayout by lens(Configuration.appearance.disableLayout)

        /**
         *  Change the presentation direction to be RTL
         */
        var rtl by lens(Configuration.appearance.rtl)

        /**
         * Enable the slide overview mode
         */
        var overview by lens(Configuration.appearance.overview)

        /**
         * Enable keyboard shortcuts for navigation
         */
        var keyboard by lens(Configuration.behavior.keyboard)

        /**
         * Loop the presentation
         */
        var loop by lens(Configuration.behavior.loop)

        /**
         * Use 1 based indexing for # links to match slide number (default is zero
         * based)
         */
        var hashOneBasedIndex by lens(Configuration.behavior.hashOneBasedIndex)

        /**
         * Add the current slide number to the URL hash so that reloading the
         * page/copying the URL will return you to the same slide
         */
        var hash by lens(Configuration.behavior.hash)

        /**
         * Flags if we should monitor the hash and change slides accordingly
         */
        var respondToHashChanges by lens(Configuration.behavior.respondToHashChanges)

        /**
         * Enable support for jump-to-slide navigation shortcuts
         */
        var jumpToSlide by lens(Configuration.behavior.jumpToSlide)

        /**
         * Push each slide change to the browser history.  Implies `hash: true`
         */
        var history by lens(Configuration.behavior.history)

        /**
         * Focuses body when page changes visibility to ensure keyboard shortcuts work
         */
        var focusBodyOnPageVisibilityChange by lens(Configuration.behavior.focusBodyOnPageVisibilityChange)

        /**
         * Hide cursor if inactive
         */
        var hideInactiveCursor by lens(Configuration.behavior.hideInactiveCursor)

        /**
         * Can be used to globally disable auto-animation
         */
        var autoAnimate by lens(Configuration.animation.autoAnimate)

        /**
         * Default settings for our auto-animate transitions, can be
         * overridden per-slide or per-element via data arguments
         */
        var autoAnimateEasing by lens(Configuration.animation.autoAnimateEasing)

        /**
         * Default settings for our auto-animate transitions, can be
         * overridden per-slide or per-element via data arguments
         */
        var autoAnimateDuration by lens(Configuration.animation.autoAnimateDuration)

        /**
         * Default settings for our auto-animate transitions, can be
         * overridden per-slide or per-element via data arguments
         */
        var autoAnimateUnmatched by lens(Configuration.animation.autoAnimateUnmatched)

        /**
         * Transition style
         */
        var transition by lens(Configuration.animation.transition)

        /**
         * Transition speed
         */
        var transitionSpeed by lens(Configuration.animation.transitionSpeed)

        /**
         * Transition style for full page slide backgrounds
         */
        var backgroundTransition by lens(Configuration.animation.backgroundTransition)

        /**
         * Parallax background image. CSS syntax, e.g. "a.jpg"
         */
        var parallaxBackgroundImage by lens(Configuration.animation.parallaxBackgroundImage)

        /**
         * Parallax background size. CSS syntax, e.g. "3000px 2000px"
         */
        var parallaxBackgroundSize by lens(Configuration.animation.parallaxBackgroundSize)
        var parallaxBackgroundRepeat by lens(Configuration.animation.parallaxBackgroundRepeat)

        fun additionalCss(block: CssBuilder.() -> Unit) {
            configuration.appearance.additionalCssStyleBuilder.block()
        }
    }

    private val slidesBuilder = SlidesBuilder()
    private val configurationBuilder = ConfigurationBuilder()
    private val metaBuilder = MetaBuilder()

    fun build(): RevealKt {
        return RevealKt(
            title = title,
            slides = slidesBuilder.slides,
            configuration = configurationBuilder.configuration,
            meta = metaBuilder.meta,
            headModifier = headModifier
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

    fun head(block: HEAD.() -> Unit) {
        headModifier = block
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
