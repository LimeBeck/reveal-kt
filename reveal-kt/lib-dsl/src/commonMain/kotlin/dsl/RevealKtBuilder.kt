package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.elements.slides.MarkdownSlide
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator

class RevealKtBuilder(
    var title: String = "RevealKt"
) {
    class SlidesBuilder : SlidesHolder {
        override val slides = mutableListOf<Slide>()
    }

    class ConfigurationBuilder {
        internal var configuration = RevealKt.defaultConfiguration

        var controls: Boolean
            get() = configuration.controls
            set(value) {
                configuration = configuration.copy(controls = value)
            }

        var progress: Boolean
            get() = configuration.progress
            set(value) {
                configuration = configuration.copy(progress = value)
            }

        var history: Boolean
            get() = configuration.history
            set(value) {
                configuration = configuration.copy(history = value)
            }

        var center: Boolean
            get() = configuration.center
            set(value) {
                configuration = configuration.copy(center = value)
            }

        var touch: Boolean
            get() = configuration.touch
            set(value) {
                configuration = configuration.copy(touch = value)
            }

        var theme: RevealKt.Configuration.Theme
            get() = configuration.theme
            set(value) {
                configuration = configuration.copy(theme = value)
            }

        var additionalCssStyle: String?
            get() = configuration.additionalCssStyle
            set(value) {
                configuration = configuration.copy(additionalCssStyle = value)
            }
    }

    private val slidesBuilder = SlidesBuilder()
    private val configurationBuilder = ConfigurationBuilder()

    fun build(): RevealKt {
        return RevealKt(
            title = title,
            slides = slidesBuilder.slides,
            configuration = configurationBuilder.configuration,
        )
    }

    fun slides(block: SlidesBuilder.() -> Unit) {
        slidesBuilder.block()
    }

    fun configuration(block: ConfigurationBuilder.() -> Unit) {
        configurationBuilder.block()
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
