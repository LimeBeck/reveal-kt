package dev.limebeck.revealkt.core

import dev.limebeck.revealkt.elements.slides.Slide
import kotlinx.html.*

data class RevealKt(
    val title: String,
    val slides: List<Slide>,
    val configuration: Configuration = defaultConfiguration,
) {

    companion object {
        val defaultConfiguration = Configuration()
    }

    data class Configuration(
        val additionalLinks: List<String> = listOf(),
        val controls: Boolean = true,
        val progress: Boolean = true,
        val history: Boolean = true,
        val center: Boolean = true,
        val touch: Boolean = true,
        val autoAnimateDuration: Double = 0.5,
        val theme: Theme = Theme.Predefined.BLACK
    ) {

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
                ;
            }

            data class Custom(
                val cssLink: String
            ) : Theme
        }
    }
//
//    data class Plugin(
//        val name: String,
//        val cdnUrl: String,
//    )
}