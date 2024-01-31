package dev.limebeck.application

import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

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
    val resource = classLoader.getResource(path).toURI()
    FileSystems.newFileSystem(resource, mapOf("create" to "true"))
    return Path.of(resource).listDirectoryEntries().map {
        it
    }
}

fun Path.isFont() = name.endsWith(".woff")
        || name.endsWith(".eot")
        || name.endsWith(".ttf")
