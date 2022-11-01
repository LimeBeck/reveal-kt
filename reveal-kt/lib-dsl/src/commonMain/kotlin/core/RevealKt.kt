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
            Plugin("RevealNotes", "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/plugin/notes/notes.min.js")
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
    )

    data class Plugin(
        val name: String,
        val cdnUrl: String,
    )

    fun render(tag: HTML) = with(tag) {
        head {
            meta {
                charset = "utf-8"
            }
            title {
                +this@RevealKt.title
            }
            link {
                rel = "stylesheet"
                href = "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/reset.min.css"
            }
            link {
                rel = "stylesheet"
                href = "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/reveal.min.css"
            }
            link {
                rel = "stylesheet"
                href = "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/theme/night.min.css"
                id = "theme"
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
            script(src = "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/reveal.min.js") {}
            script(src = "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/plugin/highlight/highlight.min.js") {}

            plugins.forEach {
                script(src = it.cdnUrl) {}
            }

            script(type = ScriptType.textJavaScript) {
                unsafe {
                    raw(
                        """
                            Reveal.initialize({
                                controls: ${configuration.controls},
                                progress: ${configuration.progress},
                                history: ${configuration.history},
                                center: ${configuration.center},
                                touch: ${configuration.touch},
                                autoAnimateDuration: ${configuration.autoAnimateDuration},
                                plugins: [ RevealHighlight, ${plugins.joinToString(", "){ it.name }} ]
                            });
                        """.trimIndent()
                    )
                }
            }
        }
    }
}