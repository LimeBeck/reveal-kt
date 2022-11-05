package dev.limebeck.application

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dev.limebeck.revealkt.dsl.revealKt
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
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

suspend fun ApplicationCall.respondPresentation(title: String = "RevealKt", block: RevealKtBuilder.() -> Unit) {
    val presentation = revealKt(title, block).build()

    respondHtml {
        presentation.render(this)
    }
}

suspend fun ApplicationCall.respondPresentation(presentationBuilder: RevealKtBuilder) {
    val presentation = presentationBuilder.build()

    respondHtml {
        presentation.render(this)
    }
}

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("ServerLogger")
    val scriptLoader = RevealKtScriptLoader()
    embeddedServer(CIO, host = "0.0.0.0", port = 8080) {
        routing {
            get("/{resourceName}") {
                val resourceName = call.parameters["resourceName"]!!
                logger.info("<955693ee> Get resource $resourceName")
                call.respondText(
                    contentType = ContentType.Text.JavaScript,
                    status = HttpStatusCode.OK
                ) {
                    withContext(Dispatchers.IO) {
                        this::class.java.classLoader.getResourceAsStream(resourceName)?.readAllBytes()
                            ?: throw NotFoundException()
                    }.decodeToString()
                }
            }
            get("/") {
                val time = measureTime {
                    val loadResult = scriptLoader.loadScript(
                        File("./reveal-kt/app/scripts/TestPresentation.reveal.kts")
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