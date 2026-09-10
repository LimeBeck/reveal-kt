package dev.limebeck.application

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.limebeck.application.commands.*
import dev.limebeck.revealkt.RevealkConfig

fun main(args: Array<String>) = RevealKtCliApplication()
    .subcommands(
        RunServer(),
        Doctor(),
        InitTemplate(),
        BundleToStatic(),
        RenderPdf(),
        Chrome().subcommands(
            InstallChrome(),
            UninstallChrome(),
        ),
    )
    .completionOption()
    .main(args)

class RevealKtCliApplication : CliktCommand() {
    override val printHelpOnEmptyArgs = true
    override fun help(context: Context) = """
           RevealKt CLI
           
           Application version ${RevealkConfig.version}
           """.trimIndent()
    override fun run() = Unit
}
