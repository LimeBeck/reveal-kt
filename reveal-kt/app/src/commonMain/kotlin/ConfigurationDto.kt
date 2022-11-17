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
    @Contextual
    val theme: Theme,
    val additionalCssStyle: String?,
    val additionalLinks: List<String>,
) {
    constructor(configuration: RevealKt.Configuration) : this(
        controls = configuration.controls,
        progress = configuration.progress,
        history = configuration.history,
        center = configuration.center,
        touch = configuration.touch,
        autoAnimateDuration = configuration.autoAnimateDuration,
        theme = Theme.of(configuration.theme),
        additionalCssStyle = configuration.additionalCssStyle,
        additionalLinks = configuration.additionalLinks,
    )

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

val configurationJsomMapper = Json {
    useArrayPolymorphism = true
}