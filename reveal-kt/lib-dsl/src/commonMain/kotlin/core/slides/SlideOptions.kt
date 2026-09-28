package dev.limebeck.revealkt.elements.slides

import dev.limebeck.revealkt.core.RevealKt.Configuration.Transition
import dev.limebeck.revealkt.core.RevealKt.Configuration.TransitionSpeed
import dev.limebeck.revealkt.utils.assetUrl
import kotlinx.html.HtmlBlockTag

/**
 * Full-slide background. Relative [image] and [video] paths point into `assets/`, like `img`.
 * See https://revealjs.com/backgrounds/.
 */
data class SlideBackground(
    val color: String? = null,
    val image: String? = null,
    val size: String? = null,
    val position: String? = null,
    val repeat: String? = null,
    val opacity: Double? = null,
    val gradient: String? = null,
    val video: String? = null,
    val videoLoop: Boolean = false,
    val videoMuted: Boolean = false,
    val iframe: String? = null,
    val interactive: Boolean = false,
)

/** Per-slide settings that override the presentation configuration. */
data class SlideOptions(
    val background: SlideBackground? = null,
    val transition: Transition? = null,
    val transitionSpeed: TransitionSpeed? = null,
    val backgroundTransition: Transition? = null,
    /** Hidden slides are skipped unless `showHiddenSlides` is enabled. */
    val hidden: Boolean = false,
)

internal fun HtmlBlockTag.renderSlideOptions(options: SlideOptions) {
    fun set(name: String, value: Any?) {
        if (value != null) attributes[name] = value.toString()
    }

    options.background?.let { background ->
        set("data-background-color", background.color)
        set("data-background-image", background.image?.let(::assetUrl))
        set("data-background-size", background.size)
        set("data-background-position", background.position)
        set("data-background-repeat", background.repeat)
        set("data-background-opacity", background.opacity)
        set("data-background-gradient", background.gradient)
        set("data-background-video", background.video?.let(::assetUrl))
        if (background.videoLoop) set("data-background-video-loop", "")
        if (background.videoMuted) set("data-background-video-muted", "")
        set("data-background-iframe", background.iframe)
        if (background.interactive) set("data-background-interactive", "")
    }
    set("data-transition", options.transition?.value)
    set("data-transition-speed", options.transitionSpeed?.value)
    set("data-background-transition", options.backgroundTransition?.value)
    if (options.hidden) set("data-visibility", "hidden")
}
