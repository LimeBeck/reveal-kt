package dev.limebeck.revealkt.core

import arrow.optics.optics
import dev.limebeck.revealkt.elements.slides.Slide

@optics
data class RevealKt(
    val title: String,
    val slides: List<Slide>,
    val configuration: Configuration = defaultConfiguration,
    val meta: Meta = defaultMeta,
) {

    companion object {
        val defaultConfiguration = Configuration()
        val defaultMeta = Meta()
    }

    @optics
    data class Configuration(
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
        val navigationMode: NavigationMode = NavigationMode.DEFAULT,

        /**
         * Randomizes the order of slides each time the presentation loads
         */
        val shuffle: Boolean = false,

        /**
         * Turns fragments on and off globally
         */
        val fragments: Boolean = true,

        /**
         * Flags whether to include the current fragment in the URL,
         * 	so that reloading brings you to the same fragment position
         */
        val fragmentInURL: Boolean = true,

        /**
         * Flags if we should show a help overlay when the question-mark
         * key is pressed
         */
        val help: Boolean = true,

        /**
         * Flags if it should be possible to pause the presentation (blackout)
         */
        val pause: Boolean = true,

        /**
         * Controls automatic progression to the next slide
         *  - 0:      Auto-sliding only happens if the data-autoslide HTML attribute
         *            is present on the current slide or fragment
         *  - 1+:     All slides will progress automatically at the given interval
         *  - <0:  No auto-sliding, even if data-autoslide is present
         */
        val autoSlide: Double = 0.0,

        /**
         * Stop auto-sliding after user input
         */
        val autoSlideStoppable: Boolean = true,

        /**
         * Specify the average time in seconds that you think you will spend
         * presenting each slide. This is used to show a pacing timer in the
         * speaker view
         */
        val defaultTiming: Int = 0,

        /**
         * Enable slide navigation via mouse wheel
         */
        val mouseWheel: Boolean = false,

        /**
         * Opens links in an iframe preview overlay
         * Add `data-preview-link` and `data-preview-link="false"` to customise each link
         * individually
         */
        val previewLinks: Boolean = false,

        /**
         * Prints each fragment on a separate slide
         */
        val pdfSeparateFragments: Boolean = true,
        val animation: Animation = Animation(),
        val behavior: Behavior = Behavior(),
        val appearance: Appearance = Appearance(),
    ) {
        @optics
        data class Appearance(
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

            /**
             *  Can be used to initialize reveal.js in one of the following views:
             * 	- print:   Render the presentation so that it can be printed to PDF
             * 	- scroll:  Show the presentation as a tall scrollable page with scroll
             * 	           triggered animations
             */
            val view: View = View.REGULAR,
            val theme: Theme = Theme.Predefined.BLACK,

            /**
             * Vertical centering of slides
             */
            val center: Boolean = true,

            /**
             * Enables touch navigation on devices with touch input
             */
            val touch: Boolean = true,

            /**
             * Display a presentation progress bar
             */
            val progress: Boolean = true,

            /**
             * Flags if speaker notes should be visible to all viewers
             */
            val showNotes: Boolean = false,

            /**
             * Flags if slides with data-visibility="hidden" should be kep visible
             */
            val showHiddenSlides: Boolean = false,

            /**
             * Disables the default reveal.js slide layout (scaling and centering)
             * so that you can use custom CSS layout
             */
            val disableLayout: Boolean = false,

            /**
             *  Change the presentation direction to be RTL
             */
            val rtl: Boolean = false,

            /**
             * Enable the slide overview mode
             */
            val overview: Boolean = true,
        ) {
            companion object;
        }

        @optics
        data class Behavior(
            /**
             * Enable keyboard shortcuts for navigation
             */
            val keyboard: Boolean = true,

            /**
             * Loop the presentation
             */
            val loop: Boolean = false,

            /**
             * Use 1 based indexing for # links to match slide number (default is zero
             * based)
             */
            val hashOneBasedIndex: Boolean = false,

            /**
             * Add the current slide number to the URL hash so that reloading the
             * page/copying the URL will return you to the same slide
             */
            val hash: Boolean = false,

            /**
             * Flags if we should monitor the hash and change slides accordingly
             */
            val respondToHashChanges: Boolean = false,

            /**
             * Enable support for jump-to-slide navigation shortcuts
             */
            val jumpToSlide: Boolean = false,

            /**
             * Push each slide change to the browser history.  Implies `hash: true`
             */
            val history: Boolean = false,

            /**
             * Focuses body when page changes visibility to ensure keyboard shortcuts work
             */
            val focusBodyOnPageVisibilityChange: Boolean = true,

            /**
             * Hide cursor if inactive
             */
            val hideInactiveCursor: Boolean = true,
        ) {
            companion object;
        }

        @optics
        data class Animation(
            /**
             * Can be used to globally disable auto-animation
             */
            val autoAnimate: Boolean = true,

            /**
             * Default settings for our auto-animate transitions, can be
             * overridden per-slide or per-element via data arguments
             */
            val autoAnimateEasing: String = "ease",

            /**
             * Default settings for our auto-animate transitions, can be
             * overridden per-slide or per-element via data arguments
             */
            val autoAnimateDuration: Double = 0.5,

            /**
             * Default settings for our auto-animate transitions, can be
             * overridden per-slide or per-element via data arguments
             */
            val autoAnimateUnmatched: Boolean = true,

            /**
             * Transition style
             */
            val transition: Transition = Transition.SLIDE,

            /**
             * Transition speed
             */
            val transitionSpeed: TransitionSpeed = TransitionSpeed.DEFAULT,

            /**
             * Transition style for full page slide backgrounds
             */
            val backgroundTransition: Transition = Transition.SLIDE,

            /**
             * Parallax background image. CSS syntax, e.g. "a.jpg"
             */
            val parallaxBackgroundImage: String = "",

            /**
             * Parallax background size. CSS syntax, e.g. "3000px 2000px"
             */
            val parallaxBackgroundSize: String = "",
            val parallaxBackgroundRepeat: Repeat = Repeat.NONE
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

        enum class Repeat(val value: String) {
            REPEAT("repeat"),
            REPEAT_X("repeat-x"),
            REPEAT_Y("repeat-y"),
            NO_REPEAT("no-repeat"),
            INITIAL("initial"),
            INHERIT("inherit"),
            NONE("")
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