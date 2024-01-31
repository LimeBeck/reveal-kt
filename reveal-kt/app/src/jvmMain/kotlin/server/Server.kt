package dev.limebeck.application.server

import dev.limebeck.application.filesWatcher.UpdatedFile
import dev.limebeck.application.filesWatcher.watchFilesRecursive
import dev.limebeck.application.printToConsole
import dev.limebeck.revealkt.RevealkConfig
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.logging.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.util.*
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue


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

@OptIn(FlowPreview::class)
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
        updatedFilesStateFlow
            .filterNotNull()
            .filter { sf ->
                sf.any { it.path.contains(config.script.name) }
                        || sf.any { it.path.contains("assets") }
            }.collect {
                val result = measureTimedValue {
                    val loadResult = scriptLoader.loadScript(config.script)
                    renderLoadResult(loadResult)
                }
                logger.info("<00596867> Render time: ${result.duration}")
                renderedTemplateStateFlow.emit(result.value)
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
                    logger.error("<2f75b6c6> Page not found", cause)
                    call.respondText(cause.asHtml(), ContentType.Text.Html, status = HttpStatusCode.NotFound)
                }

                exception<Throwable> { call, cause ->
                    logger.error("<2c1b0315> Internal error", cause)
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
                                ?: this::class.java.classLoader.getResourceAsStream("js/$resourceName")?.readAllBytes()
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
                val url = "http://${config.server.host}:${config.server.port}"

                """
                    RevealKt started at $url
                    Start duration: ${startTime.elapsedNow()}
                    Application version: ${RevealkConfig.version}
                """.trimIndent().printToConsole(minRowLength = 60)

                try {
                    if (Desktop.isDesktopSupported()) {
                        val desktop = Desktop.getDesktop()
                        desktop.browse(URI.create(url))
                    }
                } catch (t: Throwable) {
                    // Ignore
                }
            }
        }
    }).start(wait = true)
}