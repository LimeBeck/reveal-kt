package dev.limebeck.application

import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dev.limebeck.revealkt.dsl.revealKt
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.server.ConfigurationDto
import dev.limebeck.revealkt.server.configurationJsomMapper
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString

fun Throwable.asHtml(): String = """
    <html>
        <head>
            <link href="https://cdn.jsdelivr.net/npm/water.css@2/out/water.css" rel="stylesheet"/>
            <title>Rendering Error</title>
        </head>
        <body>
            <h1>ERROR</h1>
            <h3>${message ?: this.toString()}</h3>
            <p>Additional error info</p>
            <pre>
                <code>
                    ${stackTraceToString()}
                </code>
            </pre>
        </body>
    </html>
""".trimIndent()

suspend fun ApplicationCall.respondPresentation(title: String = "RevealKt", block: RevealKtBuilder.() -> Unit) {
    val presentation = revealKt(title, block).build()

    respondHtml {
        render(presentation)
    }
}

suspend fun ApplicationCall.respondPresentation(presentationBuilder: RevealKtBuilder) {
    val presentation = presentationBuilder.build()

    respondHtml {
        render(presentation)
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
    val configuration = presentation.configuration
    head {
        meta {
            charset = "utf-8"
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
                    const configurationJson = ${configurationJsomMapper.encodeToString(ConfigurationDto(configuration))}
                    """
                )
            }
        }

        script(src = "revealkt.js") { }
    }
}
