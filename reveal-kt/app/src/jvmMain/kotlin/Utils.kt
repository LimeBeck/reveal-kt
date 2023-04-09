package dev.limebeck.application

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
