package dev.limebeck.application

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dev.limebeck.revealkt.dsl.revealKt
import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.elements.slides.Slide
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import dev.limebeck.revealkt.server.ConfigurationDto
import dev.limebeck.revealkt.server.configurationJsomMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.*
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlinx.serialization.encodeToString

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

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("ServerLogger")
    val basePath = "./reveal-kt/app/scripts"
    val scriptLoader = RevealKtScriptLoader()
    embeddedServer(CIO, host = "0.0.0.0", port = 8080) {
        routing {
            get("assets/{assetName}") {
                val assetName = call.parameters["assetName"]!!
                logger.info("<2e9bb124> Get asset $assetName")
                call.respondFile(baseDir = File("$basePath/assets"), fileName = assetName)
            }

            get("/{resourceName}") {
                val resourceName = call.parameters["resourceName"]!!
                logger.info("<955693ee> Get resource $resourceName")

                call.respondBytes(
                    contentType = ContentType.defaultForFilePath(resourceName),
                    status = HttpStatusCode.OK
                ) {
                    withContext(Dispatchers.IO) {
                        this::class.java.classLoader.getResourceAsStream(resourceName)?.readAllBytes()
                            ?: throw NotFoundException()
                    }
                }
            }

            get("/") {
                val time = measureTime {
                    val loadResult = scriptLoader.loadScript(
                        File("$basePath/TestPresentation.reveal.kts")
                    )
                    when (loadResult) {
                        is RevealKtScriptLoader.LoadResult.Success -> {
                            call.respondPresentation(loadResult.value)
                        }

                        is RevealKtScriptLoader.LoadResult.Error -> {
                            call.respondHtml {
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
                        }
                    }
                }
                logger.info("<82363b2a> Processed with $time")
            }
        }
    }.start(wait = true)
}