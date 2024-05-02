package dev.limebeck.revealkt.core

import arrow.optics.optics
import dev.limebeck.revealkt.elements.slides.Slide

@optics data class RevealKt(
    val title: String,
    val slides: List<Slide>,
    val configuration: Configuration = defaultConfiguration,
    val meta: Meta = defaultMeta,
) {

    companion object {
        val defaultConfiguration = Configuration()
        val defaultMeta = Meta()
    }

    @optics data class Configuration(
        /**
         * Display presentation control arrows
         */
        val controls: Boolean = true,
        /**
         * Help the user learn the controls by providing hints, for example by
         * bouncing the down arrow when they first encounter a vertical slide
         */
        val controlsTutorial: Boolean = true,
        /**
         * Determines where controls appear, "edges" or "bottom-right"
         */
        val controlsLayout: ControlsLayout = ControlsLayout.BOTTOM_RIGHT,
        /**
         *   Visibility rule for backwards navigation arrows; "faded", "hidden"
         *   or "visible"
         */
        val controlsBackArrows: ControlsBackArrows = ControlsBackArrows.FADED,
        val navigationMode: NavigationMode = NavigationMode.DEFAULT,
        val shuffle: Boolean = false,
        val fragments: Boolean = true,
        val fragmentInURL: Boolean = true,
        val help: Boolean = true,
        val pause: Boolean = true,
        val autoSlide: Double = 0.0,
        val autoSlideStoppable: Boolean = true,
        val defaultTiming: Int = 0,
        val mouseWheel: Boolean = false,
        val previewLinks: Boolean = false,
        val postMessage: Boolean = true,
        val postMessageEvents: Boolean = false,
        val pdfSeparateFragments: Boolean = true,
        val animation: Animation = Animation(),
        val behavior: Behavior = Behavior(),
        val appearance: Appearance = Appearance(),
    ) {
        @optics data class Appearance(
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
            val slideNumber: SlideNumber = SlideNumber.Disable,
            /**
             *  Can be used to limit the contexts in which the slide number appears
             *  - "all":      Always show the slide number
             *  - "print":    Only when printing to PDF
             *  - "speaker":  Only in the speaker view
            */
            val showSlideNumber: ShowSlideNumber = ShowSlideNumber.ALL,
            val additionalCssStyle: String = "",
            val view: View = View.REGULAR,
            val theme: Theme = Theme.Predefined.BLACK,
            val center: Boolean = true,
            val touch: Boolean = true,
            /**
             * Display a presentation progress bar
             */
            val progress: Boolean = true,
            val showNotes: Boolean = false,
            val disableLayout: Boolean = false,
            val rtl: Boolean = false,
            val overview: Boolean = true,
        ) {
            companion object;
        }
        @optics data class Behavior(
            val keyboard: Boolean = true,
            val loop: Boolean = false,
            val hashOneBasedIndex: Boolean = false,
            val hash: Boolean = false,
            val respondToHashChanges: Boolean = false,
            val jumpToSlide: Boolean = false,
            val history: Boolean = false,
            val focusBodyOnPageVisibilityChange: Boolean = true,
            val hideInactiveCursor: Boolean = true,
        ) {
            companion object;
        }

        @optics data class Animation(
            val autoAnimate: Boolean = true,
            val autoAnimateEasing: String = "ease",
            val autoAnimateDuration: Double = 0.5,
            val autoAnimateUnmatched: Boolean = true,
            val transition: Transition = Transition.SLIDE,
            val transitionSpeed: TransitionSpeed = TransitionSpeed.DEFAULT,
            val backgroundTransition: Transition = Transition.SLIDE,
        ) {
            companion object;
        }

        companion object;
        enum class ControlsLayout(val value: String) {
            EDGES("edges"),
            BOTTOM_RIGHT("bottom-right"),
        }

        enum class ControlsBackArrows(val value: String) {
            FADED("faded"),
            HIDDEN("hidden"),
            VISIBLE("visible"),
        }

        sealed interface SlideNumber {
            data object Disable : SlideNumber
            data object Enable : SlideNumber
            data class Custom(val format: String) : SlideNumber
        }

        enum class ShowSlideNumber(val value: String) {
            ALL("all"),
            PRINT("print"),
            SPEAKER("speaker")
        }

        enum class NavigationMode(val value: String) {
            DEFAULT("default"),
            LINEAR("linear"),
            GRID("grid"),
        }

        enum class Transition(val value: String) {
            NONE("none"),
            FADE("fade"),
            SLIDE("slide"),
            CONVEX("convex"),
            CONCAVE("concave"),
            ZOOM("zoom"),
        }

        enum class TransitionSpeed(val value: String) {
            DEFAULT("default"),
            FAST("fast"),
            SLOW("slow")
        }

        enum class View {
            SCROLL,
            REGULAR
        }

        sealed interface Theme {
            enum class Predefined : Theme {
                BEIGE,
                BLACK,
                BLOOD,
                LEAGUE,
                MOON,
                NIGHT,
                SERIF,
                SIMPLE,
                SKY,
                SOLARIZED,
                WHITE,
                DRACULA
                ;
            }

            data class Custom(
                val cssLink: String,
            ) : Theme
        }
    }

    data class Meta(
        val description: String? = null,
        val language: String = "en",
    )
}