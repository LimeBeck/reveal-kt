package dev.limebeck.revealkt.server

import dev.limebeck.revealkt.core.RevealKt
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ConfigurationDto(
    val controls: Boolean,
    val progress: Boolean,
    val history: Boolean,
    val center: Boolean,
    val touch: Boolean,
    val autoAnimateDuration: Double,
    val view: String?,
    @Contextual
    val theme: Theme,
    val additionalCssStyle: String?,
    val controlsTutorial: Boolean,
    val controlsLayout: String,
    val controlsBackArrows: String,
    @Contextual
    val slideNumber: SlideNumber,
    val showSlideNumber: String,
    val hashOneBasedIndex: Boolean,
    val hash: Boolean,
    val respondToHashChanges: Boolean,
    val jumpToSlide: Boolean,
    val keyboard: Boolean,
    val disableLayout: Boolean,
    val overview: Boolean,
    val loop: Boolean,
    val rtl: Boolean,
    val navigationMode: String,
    val shuffle: Boolean,
    val fragments: Boolean,
    val fragmentInURL: Boolean,
    val help: Boolean,
    val pause: Boolean,
    val showNotes: Boolean,
    val autoAnimate: Boolean,
    val autoAnimateEasing: String,
    val autoAnimateUnmatched: Boolean,
    val autoSlide: Double?,
    val autoSlideStoppable: Boolean,
    val defaultTiming: Int?,
    val mouseWheel: Boolean?,
    val previewLinks: Boolean,
    val postMessage: Boolean,
    val postMessageEvents: Boolean,
    val focusBodyOnPageVisibilityChange: Boolean,
    val transition: String,
    val transitionSpeed: String,
    val backgroundTransition: String,
    val pdfSeparateFragments: Boolean,
    val hideInactiveCursor: Boolean,
) {
    constructor(configuration: RevealKt.Configuration) : this(
        controls = configuration.controls,
        progress = configuration.appearance.progress,
        history = configuration.behavior.history,
        center = configuration.appearance.center,
        touch = configuration.appearance.touch,
        autoAnimateDuration = configuration.animation.autoAnimateDuration,
        view = when (configuration.appearance.view) {
            RevealKt.Configuration.View.SCROLL -> "scroll"
            RevealKt.Configuration.View.REGULAR -> null
        },
        theme = Theme.of(configuration.appearance.theme),
        additionalCssStyle = configuration.appearance.additionalCssStyle,
        controlsTutorial = configuration.controlsTutorial,
        controlsLayout = configuration.controlsLayout.value,
        controlsBackArrows = configuration.controlsBackArrows.value,
        slideNumber = SlideNumber.of(configuration.appearance.slideNumber),
        showSlideNumber = configuration.appearance.showSlideNumber.value,
        hashOneBasedIndex = configuration.behavior.hashOneBasedIndex,
        hash = configuration.behavior.hash,
        respondToHashChanges = configuration.behavior.respondToHashChanges,
        jumpToSlide = configuration.behavior.jumpToSlide,
        keyboard = configuration.behavior.keyboard,
        disableLayout = configuration.appearance.disableLayout,
        overview = configuration.appearance.overview,
        loop = configuration.behavior.loop,
        rtl = configuration.appearance.rtl,
        navigationMode = configuration.navigationMode.value,
        shuffle = configuration.shuffle,
        fragments = configuration.fragments,
        fragmentInURL = configuration.fragmentInURL,
        help = configuration.help,
        pause = configuration.pause,
        showNotes = configuration.appearance.showNotes,
        autoAnimate = configuration.animation.autoAnimate,
        autoAnimateEasing = configuration.animation.autoAnimateEasing,
        autoAnimateUnmatched = configuration.animation.autoAnimateUnmatched,
        autoSlide = configuration.autoSlide,
        autoSlideStoppable = configuration.autoSlideStoppable,
        defaultTiming = configuration.defaultTiming,
        mouseWheel = configuration.mouseWheel,
        previewLinks = configuration.previewLinks,
        postMessage = configuration.postMessage,
        postMessageEvents = configuration.postMessageEvents,
        focusBodyOnPageVisibilityChange = configuration.behavior.focusBodyOnPageVisibilityChange,
        transition = configuration.animation.transition.value,
        transitionSpeed = configuration.animation.transitionSpeed.value,
        backgroundTransition = configuration.animation.backgroundTransition.value,
        pdfSeparateFragments = configuration.pdfSeparateFragments,
        hideInactiveCursor = configuration.behavior.hideInactiveCursor,
    )

    @Polymorphic
    @Serializable
    sealed interface SlideNumber {
        companion object {
            fun of(slideNumber: RevealKt.Configuration.SlideNumber): SlideNumber = when (slideNumber) {
                RevealKt.Configuration.SlideNumber.Enable -> Enable
                RevealKt.Configuration.SlideNumber.Disable -> Disable
                is RevealKt.Configuration.SlideNumber.Custom -> Custom(slideNumber.format)
            }
        }

        @Serializable
        @SerialName("Disable")
        data object Disable : SlideNumber

        @Serializable
        @SerialName("Enable")
        data object Enable : SlideNumber

        @Serializable
        @SerialName("Custom")
        data class Custom(val format: String) : SlideNumber
    }

    @Polymorphic
    @Serializable
    sealed interface Theme {
        companion object {
            fun of(theme: RevealKt.Configuration.Theme) = when (theme) {
                is RevealKt.Configuration.Theme.Predefined -> Predefined.of(theme)
                is RevealKt.Configuration.Theme.Custom -> Custom(theme.cssLink)
            }
        }

        @Serializable
        @SerialName("Predefined")
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

            companion object {
                fun of(theme: RevealKt.Configuration.Theme.Predefined) =
                    when (theme) {
                        RevealKt.Configuration.Theme.Predefined.BEIGE -> BEIGE
                        RevealKt.Configuration.Theme.Predefined.BLACK -> BLACK
                        RevealKt.Configuration.Theme.Predefined.BLOOD -> BLOOD
                        RevealKt.Configuration.Theme.Predefined.LEAGUE -> LEAGUE
                        RevealKt.Configuration.Theme.Predefined.MOON -> MOON
                        RevealKt.Configuration.Theme.Predefined.NIGHT -> NIGHT
                        RevealKt.Configuration.Theme.Predefined.SERIF -> SERIF
                        RevealKt.Configuration.Theme.Predefined.SIMPLE -> SIMPLE
                        RevealKt.Configuration.Theme.Predefined.SKY -> SKY
                        RevealKt.Configuration.Theme.Predefined.SOLARIZED -> SOLARIZED
                        RevealKt.Configuration.Theme.Predefined.WHITE -> WHITE
                        RevealKt.Configuration.Theme.Predefined.DRACULA -> DRACULA
                    }
            }
        }

        @Serializable
        @SerialName("Custom")
        data class Custom(
            val cssLink: String,
        ) : Theme
    }
}

val configurationJsonMapper = Json {
    useArrayPolymorphism = true
}