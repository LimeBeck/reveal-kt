package dev.limebeck.application

import org.slf4j.Logger
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries

fun printMessage(message: String, symbol: String = "*", minRowLength: Int = 40, borderSize: Int = 2) {
    val lines = message.lines()
    val rowLength = maxOf(lines.maxOf { it.length }, minRowLength)

    fun printLines() = repeat(borderSize) {
        println(symbol.repeat(rowLength))
    }

    fun makeBorderedLine(line: String): String =
        (symbol.repeat(borderSize) + " $line ")
            .padEnd(rowLength - 2) + symbol.repeat(borderSize)

    printLines()
    lines.forEach {
        println(makeBorderedLine(it))
    }
    printLines()
}

fun String.printToConsole(symbol: String = "*", minRowLength: Int = 40, borderSize: Int = 2) =
    printMessage(this, symbol, minRowLength, borderSize)

fun getResourcesList(path: String): List<Path> {
    val classLoader = {}::class.java.classLoader
    val resource = classLoader.getResource(path)!!.toURI()
    FileSystems.newFileSystem(resource, mapOf("create" to "true"))
    return Path.of(resource).listDirectoryEntries()
}

fun Logger.debug(block: () -> String) {
    if (isDebugEnabled) {
        debug(block())
    }
}

fun Logger.info(block: () -> String) {
    if (isInfoEnabled) {
        info(block())
    }
}
