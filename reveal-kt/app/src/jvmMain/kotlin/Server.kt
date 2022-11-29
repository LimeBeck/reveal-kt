package dev.limebeck.application

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import dev.limebeck.application.filesWatcher.UpdatedFile
import dev.limebeck.application.filesWatcher.watchFilesRecursive
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.logging.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.produceIn
import kotlinx.html.*
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.io.path.pathString
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue

class Server : CliktCommand() {
    val port: Int by option(help = "Port").int().default(8080)
    val host: String by option(help = "Host").default("0.0.0.0")
    val basePath: Path? by option(help = "Script dir").path()
    val script: File by argument(help = "Script file").file(canBeDir = false, mustBeReadable = true)

    override fun run() {
        runServer(
            Config(
                server = ServerConfig(host, port),
                basePath = basePath?.pathString ?: script.parent,
                script = script
            )
        )
    }
}

fun main(args: Array<String>) = Server().completionOption().main(args)

data class Config(
    val server: ServerConfig,
    val basePath: String,
    val script: File,
)

data class ServerConfig(
    val host: String = "0.0.0.0",
    val port: Int = 8080,
)

val logger = LoggerFactory.getLogger("ServerLogger")

@OptIn(ExperimentalTime::class)
fun runServer(config: Config) {
    println("Starting application...")
    val startTime = TimeSource.Monotonic.markNow()

    val basePath = config.basePath

    val scriptLoader = RevealKtScriptLoader()

    val coroutineScope = CoroutineScope(SupervisorJob())

    val firstLoadResult = measureTimedValue {
        val loadResult = scriptLoader.loadScript(config.script)
        renderLoadResult(loadResult)
    }
    logger.info("First render took ${firstLoadResult.duration}")

    val updatedFilesStateFlow = MutableStateFlow<List<UpdatedFile>?>(null)
    val renderedTemplateStateFlow = MutableStateFlow<String>(firstLoadResult.value)

    coroutineScope.launch {
        watchFilesRecursive(basePath) {
            logger.debug("<0969400d> Files changed: $it")
            coroutineScope.launch {
                updatedFilesStateFlow.emit(it)
            }
        }
    }

    coroutineScope.launch {
        updatedFilesStateFlow.collect { sf ->
            if (sf != null && (
                        sf.any { it.path.contains(config.script.name) }
                                || sf.any { it.path.contains("assets") }
                        )
            ) {
                val result = measureTimedValue {
                    val loadResult = scriptLoader.loadScript(config.script)
                    renderLoadResult(loadResult)
                }
                logger.info("<00596867> Render time: ${result.duration}")
                renderedTemplateStateFlow.emit(result.value)
            }
        }
    }

    embeddedServer(CIO, environment = applicationEngineEnvironment {
        connector {
            host = config.server.host
            port = config.server.port
        }

        module {
            install(StatusPages) {
                exception<NotFoundException> { call, cause ->
                    logger.error("<2c1b0315> Error", cause)
                    call.respondText(cause.asHtml(), ContentType.Text.Html, status = HttpStatusCode.NotFound)
                }

                exception<Throwable> { call, cause ->
                    logger.error("<2c1b0315> Error", cause)
                    call.respondText(cause.asHtml(), ContentType.Text.Html)
                }
            }

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
                    val renderResult = renderedTemplateStateFlow.value.appendSseReloadScript()
                    call.respondText(renderResult, ContentType.Text.Html)
                }

                get("/sse") {
                    logger.debug("<7724a434> Subscribed with ${this.call.request.toLogString()}")
                    val events = renderedTemplateStateFlow
                        .drop(1)
                        .map { SseEvent(data = it, event = "PageUpdated", id = UUID.randomUUID().toString()) }
                        .produceIn(this)

                    try {
                        call.respondSse(events)
                    } catch (e: CancellationException) {
                    } finally {
                        events.cancel()
                        this.finish()
                    }
                }
            }
            environment.monitor.subscribe(ApplicationStarted) {
                printMessage(
                    """
                    Application started at http://${config.server.host}:${config.server.port}
                    Start duration: ${startTime.elapsedNow()}
                """.trimIndent(),
                    minRowLength = 60
                )
            }
        }
    }).start(wait = true)
}