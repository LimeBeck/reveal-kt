package dev.limebeck.application

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dev.limebeck.revealkt.dsl.revealKt
import dev.limebeck.revealkt.scripts.RevealKtScript
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.p
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.createJvmEvaluationConfigurationFromTemplate
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

fun BasicJvmScriptingHost.evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<RevealKtScript> {

    }
    val evaluationConfiguration = createJvmEvaluationConfigurationFromTemplate<RevealKtScript> {
        implicitReceivers(RevealKtBuilder())
    }
    return eval(
        script = scriptFile.toScriptSource(),
        compilationConfiguration = compilationConfiguration,
        evaluationConfiguration = evaluationConfiguration
    )
}

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("ServerLogger")
    val scriptingHost = BasicJvmScriptingHost()
    embeddedServer(CIO, host = "0.0.0.0", port = 8080) {
        routing {
            get("/") {
                val time = measureTime {
                    val result =
                        scriptingHost.evalFile(File("./reveal-kt/app/scripts/TestPresentation.reveal.kts"))

                    val implicitReceivers = result.valueOrNull()
                        ?.configuration
                        ?.notTransientData
                        ?.entries
                        ?.find { it.key.name == "implicitReceivers" }?.value as? List<RevealKtBuilder>

                    if (implicitReceivers != null) {
                        call.respondPresentation(implicitReceivers.first())
                    } else {
                        call.respondHtml {
                            body {
                                result.reports.forEach {
                                    div { p { +it.render() } }
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