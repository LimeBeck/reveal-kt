package dev.limebeck.application

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dev.limebeck.revealkt.dsl.revealKt
import dev.limebeck.revealkt.scripts.RevealKtEvaluationConfiguration
import dev.limebeck.revealkt.scripts.RevealKtScriptCompilationConfiguration
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.valueOrNull
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

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

fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
    return BasicJvmScriptingHost().eval(
        script = scriptFile.toScriptSource(),
        compilationConfiguration = RevealKtScriptCompilationConfiguration,
        evaluationConfiguration = RevealKtEvaluationConfiguration
    )
}

fun main(args: Array<String>) {
    embeddedServer(CIO, host = "0.0.0.0", port = 8080) {
        routing {
            get("/") {
                val result =
                    evalFile(File("./reveal-kt/app/scripts/TestPresentation.reveal.kts"))

                val implicitReceivers = result.valueOrNull()
                    ?.configuration
                    ?.notTransientData
                    ?.entries
                    ?.find { it.key.name == "implicitReceivers" }?.value as? List<RevealKtBuilder>
                    ?: throw RuntimeException("<d68440ba>")

                call.respondPresentation(implicitReceivers.first())
            }
        }
    }.start(wait = true)
}