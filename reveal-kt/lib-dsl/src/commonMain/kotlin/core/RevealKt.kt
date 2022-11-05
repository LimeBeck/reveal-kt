package dev.limebeck.revealkt.elements

import dev.limebeck.revealkt.elements.slides.Slide
import kotlinx.html.*

data class RevealKt(
    val title: String,
    val slides: List<Slide>,
    val plugins: List<Plugin> = defaultPlugins,
    val configuration: Configuration = defaultConfiguration,
) {

    companion object {
        val defaultPlugins = listOf(
            Plugin("RevealNotes", "reveal.js/plugin/notes/notes.js"),
            Plugin("RevealHighlight", "reveal.js/plugin/highlight/highlight.js")
        )
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

    data class Plugin(
        val name: String,
        val cdnUrl: String,
    )

    fun renderSlides(tag: FlowContent) = with(tag) {
        div("reveal") {
            div("slides") {
                slides.forEach {
                    it.render(this)
                }
            }
        }
    }

    fun render(tag: HTML) = with(tag) {
        head {
            meta {
                charset = "utf-8"
            }
            title {
                +this@RevealKt.title
            }
        }
        body {
            div("reveal") {
                div("slides") {
                    slides.forEach {
                        it.render(this)
                    }
                }
            }

            script {
                unsafe {
                    raw(
                        """
                            const configuration = {
                                controls: ${configuration.controls},
                                progress: ${configuration.progress},
                                history: ${configuration.history},
                                center: ${configuration.center},
                                touch: ${configuration.touch},
                                autoAnimateDuration: ${configuration.autoAnimateDuration},
                            }
                        """.trimIndent()
                    )
                }
            }

            script {
                unsafe {
                    raw(
                        """
                            const plugins = [
                                ${plugins.joinToString(",", prefix = "\"", postfix = "\""){ it.name }}
                            ]
                        """.trimIndent()
                    )
                }
            }

//            plugins.forEach {
//                script(src = it.cdnUrl) {}
//            }

            script(src = "revealkt.js") { }
        }
    }
}