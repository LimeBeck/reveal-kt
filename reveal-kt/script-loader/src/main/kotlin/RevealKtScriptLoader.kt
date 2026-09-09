package dev.limebeck.revealkt.scripts

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dsl.AssetLoader
import java.io.File
import java.util.concurrent.CancellationException
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.createJvmEvaluationConfigurationFromTemplate

class RevealKtScriptLoader {
    private val scriptingHost = BasicJvmScriptingHost()

    fun loadScript(scriptFile: File): LoadResult {
        val normalizedScript = scriptFile.absoluteFile.normalize()
        val result = try {
            scriptingHost.evalFile(normalizedScript, normalizedScript.parentFile.resolve("assets").path)
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            return failure(normalizedScript, error)
        }
        val evaluationError = result.valueOrNull()?.returnValue as? ResultValue.Error
        if (evaluationError != null) {
            return failure(normalizedScript, evaluationError.error, result.reports)
        }

        val implicitReceivers = result.valueOrNull()
            ?.configuration
            ?.get(ScriptEvaluationConfiguration.implicitReceivers)

        val builder = implicitReceivers?.filterIsInstance<RevealKtBuilder>()?.firstOrNull()

        return if (builder == null) {
            LoadResult.Error(result.reports)
        } else {
            LoadResult.Success(builder)
        }
    }

    private fun failure(script: File, error: Throwable, reports: List<ScriptDiagnostic> = emptyList()): LoadResult.Error {
        val causes = generateSequence(error) { it.cause }.take(20).toList()
        val cause = causes.last()
        val frame = causes.asReversed().asSequence().flatMap { it.stackTrace.asSequence() }
            .firstOrNull { it.fileName == script.name && it.lineNumber > 0 }
        return LoadResult.Error(reports + ScriptDiagnostic(
            code = ScriptDiagnostic.unspecifiedError,
            message = cause.toString(),
            severity = ScriptDiagnostic.Severity.ERROR,
            sourcePath = script.path,
            location = frame?.let { SourceCode.Location(SourceCode.Position(it.lineNumber, 1)) },
            exception = cause
        ))
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
