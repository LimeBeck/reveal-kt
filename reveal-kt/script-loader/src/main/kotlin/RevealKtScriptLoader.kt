package dev.limebeck.revealkt.scripts

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dev.limebeck.revealkt.dsl.AssetLoader
import java.io.File
import java.util.concurrent.CancellationException
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.createJvmEvaluationConfigurationFromTemplate

class RevealKtScriptLoader {
    private val scriptingHost = BasicJvmScriptingHost()

    /**
     * @param assetsDir directory that `loadAsset` reads from; it must match the directory served as `assets/`
     */
    fun loadScript(
        scriptFile: File,
        assetsDir: File = scriptFile.absoluteFile.normalize().parentFile.resolve("assets"),
    ): LoadResult {
        val normalizedScript = scriptFile.absoluteFile.normalize()
        val assetLoader = AssetLoader(assetsDir.absoluteFile.normalize().path, normalizedScript.parentFile.path)
        val result = try {
            scriptingHost.evalFile(normalizedScript, assetLoader)
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
            LoadResult.Success(builder, assetLoader.loadedFiles.map { it.toFile() }.toSet())
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
            val value: RevealKtBuilder,
            /** Files the script read through `loadAsset` or `codeFromFile`. */
            val dependencies: Set<File> = emptySet(),
        ) : LoadResult

        data class Error(
            val diagnostic: List<ScriptDiagnostic>
        ) : LoadResult
    }

    private fun BasicJvmScriptingHost.evalFile(scriptFile: File, assetLoader: AssetLoader): ResultWithDiagnostics<EvaluationResult> {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<RevealKtScript> { }
        val evaluationConfiguration = createJvmEvaluationConfigurationFromTemplate<RevealKtScript> {
            implicitReceivers(RevealKtBuilder(), assetLoader)
        }
        return eval(
            script = scriptFile.toScriptSource(),
            compilationConfiguration = compilationConfiguration,
            evaluationConfiguration = evaluationConfiguration
        )
    }
}
