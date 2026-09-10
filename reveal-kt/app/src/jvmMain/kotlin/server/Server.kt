package dev.limebeck.application.server

import com.github.ajalt.clikt.core.CliktError
import dev.limebeck.application.debug
import dev.limebeck.application.filesWatcher.UpdatedFile
import dev.limebeck.application.filesWatcher.watchFilesRecursive
import dev.limebeck.application.info
import dev.limebeck.application.printToConsole
import dev.limebeck.revealkt.RevealkConfig
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import dev.limebeck.revealkt.scripts.formatDiagnostics
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.logging.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.io.Closeable
import java.io.File
import java.net.URI
import java.nio.file.StandardWatchEventKinds.OVERFLOW
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.milliseconds
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

private data class RenderedPage(val revision: Long, val html: String, val error: String? = null)

private fun loadPage(script: File, loader: RevealKtScriptLoader): Result<String> = try {
    when (val result = loader.loadScript(script)) {
        is RevealKtScriptLoader.LoadResult.Success -> Result.success(renderLoadResult(result))
        is RevealKtScriptLoader.LoadResult.Error -> Result.failure(IllegalArgumentException(result.formatDiagnostics(script)))
    }
} catch (error: Exception) {
    if (error is CancellationException) throw error
    Result.failure(IllegalStateException("${script.absolutePath}: error: ${error.message}", error))
}

@OptIn(FlowPreview::class)
fun runServer(config: Config, background: Boolean = true, liveReload: Boolean = true): Closeable {
    println("Starting application...")
    val startTime = TimeSource.Monotonic.markNow()

    val basePath = Path(config.basePath).toAbsolutePath().normalize()
    val scriptPath = config.script.toPath().toAbsolutePath().normalize()
    val assetsPath = basePath.resolve("assets")

    val scriptLoader = RevealKtScriptLoader()

    val firstLoadResult = measureTimedValue {
        loadPage(config.script, scriptLoader).getOrElse { throw CliktError(it.message.orEmpty()) }
    }

    logger.info { "First render took ${firstLoadResult.duration}" }

    val serverJob = SupervisorJob()
    val coroutineScope = CoroutineScope(serverJob + Dispatchers.IO)
    val updatedFilesStateFlow = MutableStateFlow<List<UpdatedFile>?>(null)
    val renderedTemplateStateFlow = MutableStateFlow(RenderedPage(revision = 0, html = firstLoadResult.value))

    if (liveReload) {
        val roots = listOf(basePath, scriptPath.parent).distinct()
        val watchRoots = roots.filter { candidate ->
            roots.none { other -> other != candidate && candidate.startsWith(other) }
        }
        watchRoots.forEach { root ->
            coroutineScope.launch {
                watchFilesRecursive(root) { updatedFilesStateFlow.emit(it) }
            }
        }
    }

    coroutineScope.launch {
        updatedFilesStateFlow
            .filterNotNull()
            .filter { events ->
                events.any { event ->
                    val changed = Path(event.path)
                    event.type == OVERFLOW || changed == scriptPath || changed.startsWith(assetsPath)
                }
            }
            .debounce(500.milliseconds)
            .collect {
                val result = measureTimedValue {
                    loadPage(config.script, scriptLoader)
                }
                logger.info { "<00596867> Render time: ${result.duration}" }
                result.value.fold(
                    onSuccess = { html ->
                        renderedTemplateStateFlow.update { page ->
                            RenderedPage(revision = page.revision + 1, html = html)
                        }
                    },
                    onFailure = { error ->
                        val message = error.message.orEmpty()
                        logger.error(message)
                        renderedTemplateStateFlow.update { it.copy(error = message) }
                    }
                )
            }
    }

    val server = embeddedServer(CIO, configure = {
        connector {
            host = config.server.host
            port = config.server.port
        }
    }) {
        install(StatusPages) {
            exception<NotFoundException> { call, cause ->
                logger.error("<2f75b6c6> Page not found", cause)
                call.respondText(cause.asHtml(), ContentType.Text.Html, status = HttpStatusCode.NotFound)
            }

            exception<Throwable> { call, cause ->
                if (cause !is CancellationException)
                    logger.error("<2c1b0315> Internal error", cause)
                call.respondText(cause.asHtml(), ContentType.Text.Html)
            }
        }

        routing {
            staticFiles("assets/", assetsPath.toFile()) {
                enableAutoHeadResponse()
            }

            staticResources("/", "static") {
                enableAutoHeadResponse()
            }

            get("/") {
                val page = renderedTemplateStateFlow.value
                val renderResult = if (liveReload) page.html.appendSseReloadScript(page.revision) else page.html
                call.respondText(renderResult, ContentType.Text.Html)
            }

            get("/sse") {
                logger.debug { "<7724a434> Subscribed with ${this.call.request.toLogString()}" }
                val events = renderedTemplateStateFlow
                    .map { page ->
                        SseEvent(
                            data = page.error ?: page.revision.toString(),
                            event = if (page.error == null) "PageUpdated" else "RenderError"
                        )
                    }
                    .produceIn(this.call)

                try {
                    call.respondSse(events)
                } catch (e: CancellationException) {
                } finally {
                    events.cancel()
                }
            }
        }

        monitor.subscribe(ApplicationStarted) {
            val url = "http://${config.server.host}:${config.server.port}"

            """
                RevealKt started at $url
                Start duration: ${startTime.elapsedNow()}
                Application version: ${RevealkConfig.version}
            """.trimIndent().printToConsole(minRowLength = 60)

            runCatching {
                if (Desktop.isDesktopSupported() && background) {
                    val desktop = Desktop.getDesktop()
                    desktop.browse(URI.create(url))
                }
            }
        }
        monitor.subscribe(ApplicationStopped) { coroutineScope.cancel() }
    }
    val handle = Closeable {
        try {
            server.stop(100, 1000)
        } finally {
            runBlocking { serverJob.cancelAndJoin() }
        }
    }
    val shutdownHook = Thread { handle.close() }
    val runtime = Runtime.getRuntime()
    runtime.addShutdownHook(shutdownHook)
    try {
        server.start(wait = background)
    } catch (error: Throwable) {
        handle.close()
        runtime.removeShutdownHook(shutdownHook)
        throw error
    }
    return Closeable {
        handle.close()
        runtime.removeShutdownHook(shutdownHook)
    }
}
