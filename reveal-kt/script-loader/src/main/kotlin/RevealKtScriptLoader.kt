package dev.limebeck.revealkt.scripts

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dsl.AssetLoader
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.createJvmEvaluationConfigurationFromTemplate

class RevealKtScriptLoader {
    private val scriptingHost = BasicJvmScriptingHost()

    fun loadScript(scriptFile: File): LoadResult {
        val result = scriptingHost.evalFile(scriptFile, scriptFile.parentFile.resolve("assets").absolutePath)

        val implicitReceivers = result.valueOrNull()
            ?.configuration
            ?.notTransientData
            ?.entries
            ?.find { it.key.name == "implicitReceivers" }?.value as? List<*>

        val builder = implicitReceivers?.filterIsInstance<RevealKtBuilder>()?.firstOrNull()

        return if (builder == null) {
            LoadResult.Error(result.reports)
        } else {
            LoadResult.Success(builder)
        }
    }

    sealed interface LoadResult {
        data class Success(
            val value: RevealKtBuilder
        ) : LoadResult

        data class Error(
            val diagnostic: List<ScriptDiagnostic>
        ) : LoadResult
    }

    private fun BasicJvmScriptingHost.evalFile(scriptFile: File, assetPath: String): ResultWithDiagnostics<EvaluationResult> {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<RevealKtScript> { }
        val evaluationConfiguration = createJvmEvaluationConfigurationFromTemplate<RevealKtScript> {
            implicitReceivers(RevealKtBuilder(), AssetLoader(assetPath))
        }
        return eval(
            script = scriptFile.toScriptSource(),
            compilationConfiguration = compilationConfiguration,
            evaluationConfiguration = evaluationConfiguration
        )
    }
}