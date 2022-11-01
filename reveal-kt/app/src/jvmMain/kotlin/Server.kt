package dev.limebeck.application

import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.regularSlide
import dev.limebeck.revealkt.dsl.slides.slide
import dev.limebeck.revealkt.dsl.slides.verticalSlide
import core.elements.Title
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.routing.*

suspend fun ApplicationCall.respondPresentation(title: String = "RevealKt", block: RevealKtBuilder.() -> Unit) {
    val presentation = revealKt(title, block).build()

    respondHtml {
        presentation.render(this)
    }
}

fun main(args: Array<String>) {
    embeddedServer(CIO, host = "0.0.0.0", port = 8080) {
        routing {
            get("/") {
                call.respondPresentation {
                    title = "Hello from my presentation"

                    configuration {
                        controls = false
                        progress = false
                    }

                    slides {
                        regularSlide {
                            autoanimate = true
                            title { "Test 2" }
                        }
                        verticalSlide {
                            val title = Title { "Some text" }
                            slide {
                                autoanimate = true
                                +title
                                note {
                                    "Some note"
                                }
                            }
                            slide {
                                autoanimate = true
                                +title
                                title { "Updated text" }
                                note {
                                    "Some note"
                                }
                            }
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}