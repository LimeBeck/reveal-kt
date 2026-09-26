package dev.limebeck.application.server

import kotlinx.html.*
import kotlinx.html.stream.createHTML

fun Throwable.asHtml(): String = "<!DOCTYPE html>" + createHTML().html {
    head {
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/water.css@2/out/water.css")
        title { +"Rendering Error" }
    }
    body {
        h1 { +"ERROR" }
        h3 { +(message ?: this@asHtml.toString()) }
        p { +"Additional error info" }
        pre { code { +stackTraceToString() } }
    }
}
