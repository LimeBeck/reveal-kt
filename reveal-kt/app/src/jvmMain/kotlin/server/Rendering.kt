package dev.limebeck.application.server

import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import dev.limebeck.revealkt.server.ConfigurationDto
import dev.limebeck.revealkt.server.configurationJsonMapper
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlinx.serialization.encodeToString

fun renderLoadResult(loadResult: RevealKtScriptLoader.LoadResult): String {
    return "<!DOCTYPE html>" + when (loadResult) {
        is RevealKtScriptLoader.LoadResult.Success -> {
            val presentation = loadResult.value.build()
            createHTML(prettyPrint = true, xhtmlCompatible = true).apply {
                html {
                    render(presentation)
                }
            }.finalize()
        }

        is RevealKtScriptLoader.LoadResult.Error -> {
            createHTML(prettyPrint = true, xhtmlCompatible = true).apply {
                html {
                    head {
                        title { +"Render Error" }
                        link(
                            rel = "stylesheet",
                            href = "https://cdn.jsdelivr.net/npm/water.css@2/out/water.css"
                        )
                    }
                    body {
                        loadResult.diagnostic.forEach {
                            div { p { +it.render() } }
                        }
                    }
                }
            }.finalize()
        }
    }
}

fun FlowContent.renderSlides(slides: List<Slide>) {
    div("reveal") {
        div("slides") {
            slides.forEach {
                it.render(this)
            }
        }
    }
}

fun HTML.render(presentation: RevealKt) {
    lang = presentation.meta.language
    val configuration = presentation.configuration
    head {
        meta {
            charset = "utf-8"
        }

        meta(name = "title", content = presentation.title)

        presentation.meta.description?.let {
            meta("description", content = it)
        }
        title {
            +presentation.title
        }
    }
    body {
        renderSlides(presentation.slides)

        script {
            unsafe {
                raw(
                    """
                    const configurationJson = ${configurationJsonMapper.encodeToString(ConfigurationDto(configuration))}
                    """
                )
            }
        }

        script(src = "revealkt.js") { }
    }
}
