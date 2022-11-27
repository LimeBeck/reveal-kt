package dev.limebeck.application

import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import dev.limebeck.revealkt.server.ConfigurationDto
import dev.limebeck.revealkt.server.configurationJsomMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlinx.serialization.encodeToString
import java.io.IOException

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

fun renderLoadResult(loadResult: RevealKtScriptLoader.LoadResult): String {
    return when (loadResult) {
        is RevealKtScriptLoader.LoadResult.Success -> {
            val presentation = loadResult.value.build()
            createHTML(prettyPrint = true).apply {
                html {
                    render(presentation)
                }
            }.finalize()
        }

        is RevealKtScriptLoader.LoadResult.Error -> {
            createHTML(prettyPrint = true).apply {
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

fun String.appendSseReloadScript() = this + """
        <script type="text/javascript">
            var source = new EventSource('/sse');
            source.addEventListener('PageUpdated', function(e) {
                location.reload()
            }, false);
        </script>
""".trimIndent()

data class SseEvent(val data: String, val event: String? = null, val id: String? = null)

suspend fun ApplicationCall.respondSse(events: ReceiveChannel<SseEvent>) =
    coroutineScope {
        response.cacheControl(CacheControl.NoCache(null))
        respondTextWriter(contentType = ContentType.Text.EventStream) {
            events.consumeEach { event ->
                logger.debug("<0c08a270> Send event $event")
                try {
                    if (event.id != null) {
                        write("id: ${event.id}\n")
                    }
                    if (event.event != null) {
                        write("event: ${event.event}\n")
                    }
                    for (dataLine in event.data.lines()) {
                        write("data: $dataLine\n")
                    }
                    write("\n")
                    flush()
                } catch (e: IOException) {
                    logger.debug("<b785289> Closed socked. Cancel execution")
                    this@coroutineScope.cancel()
                }
            }
        }
    }

fun printMessage(message: String, symbol: String = "*", minRowLength: Int = 40, borderSize: Int = 2) {
    val lines = message.lines()
    val rowLength = maxOf(lines.maxOf { it.length }, minRowLength)

    fun printLines() = repeat(borderSize) {
        println(symbol.repeat(rowLength))
    }

    fun makeBorderedLine(line: String): String =
        (symbol.repeat(borderSize) + " $line ")
            .padEnd(rowLength - 2) + symbol.repeat(borderSize)

    printLines()
    lines.forEach {
        println(makeBorderedLine(it))
    }
    printLines()
}
