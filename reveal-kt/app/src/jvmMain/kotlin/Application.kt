package dev.limebeck.application

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) = RevealKtApplication()
    .subcommands(RunServer(), InitTemplate(), RenderToFile())
    .completionOption()
    .main(args)
