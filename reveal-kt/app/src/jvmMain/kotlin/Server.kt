package dev.limebeck.application

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import dev.limebeck.revealkt.scripts.RevealKtScriptLoader
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.*
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

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

@OptIn(ExperimentalTime::class)
fun runServer(config: Config) {
    val logger = LoggerFactory.getLogger("ServerLogger")
    val basePath = config.basePath

    val scriptLoader = RevealKtScriptLoader()

    embeddedServer(CIO, environment = applicationEngineEnvironment {
        connector {
            host = config.server.host
            port = config.server.port
        }

        module {
            install(StatusPages) {
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
                    val time = measureTime {
                        val loadResult = scriptLoader.loadScript(config.script)
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
        }
    }).start(wait = true)
}