package dev.limebeck.revealkt.scripts

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
    fileExtension = "reveal.kts",
    compilationConfiguration = RevealKtScriptCompilationConfiguration::class,
    evaluationConfiguration = RevealKtEvaluationConfiguration::class
)
abstract class RevealKtScript {
}

object RevealKtScriptCompilationConfiguration : ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
//    defaultImports("dev.limebeck.revealkt.dsl.*")
    implicitReceivers(RevealKtBuilder::class)
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})

object RevealKtEvaluationConfiguration : ScriptEvaluationConfiguration(
    {
        scriptsInstancesSharing(true)
        implicitReceivers(RevealKtBuilder())
    }
)