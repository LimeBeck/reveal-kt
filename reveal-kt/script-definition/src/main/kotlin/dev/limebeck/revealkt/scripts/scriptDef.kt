package dev.limebeck.revealkt.scripts

import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.RevealKtBuilder
import dsl.AssetLoader
import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.jvm.*
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache

@KotlinScript(
    fileExtension = "reveal.kts",
    compilationConfiguration = RevealKtScriptCompilationConfiguration::class,
    evaluationConfiguration = RevealKtEvaluationConfiguration::class,
    hostConfiguration = RevealKtKtHostConfiguration::class
)
abstract class RevealKtScript

object RevealKtScriptCompilationConfiguration : ScriptCompilationConfiguration({
    jvm {
        dependenciesFromClassContext(RevealKt::class, wholeClasspath = true)
    }
    defaultImports(DependsOn::class, Repository::class)
    defaultImports(
        "dev.limebeck.revealkt.core.elements.*",
        "dev.limebeck.revealkt.dsl.*",
        "dev.limebeck.revealkt.dsl.slides.*"
    )
    implicitReceivers(RevealKtBuilder::class, AssetLoader::class)
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    compilerOptions.append(
        "-Xadd-modules=ALL-MODULE-PATH",
    )

    // Callbacks
    refineConfiguration {
        // Process specified annotations with the provided handler
        onAnnotations(DependsOn::class, Repository::class, handler = ::configureMavenDepsOnAnnotations)
    }
})

object RevealKtEvaluationConfiguration : ScriptEvaluationConfiguration({
})

object RevealKtKtHostConfiguration : ScriptingHostConfiguration({
    jvm {
        val cacheBaseDir = findCacheBaseDir()
        if (cacheBaseDir != null)
            compilationCache(
                CompiledScriptJarsCache { script, scriptCompilationConfiguration ->
                    cacheBaseDir
                        .resolve(compiledScriptUniqueName(script, scriptCompilationConfiguration))
                }
            )
    }
})

// Handler that reconfigures the compilation on the fly
fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
        ?: return context.compilationConfiguration.asSuccess()
    return runBlocking {
        resolver.resolveFromScriptSourceAnnotations(annotations)
    }.onSuccess {
        context.compilationConfiguration.with {
            dependencies.append(JvmDependency(it))
        }.asSuccess()
    }
}

private val resolver = CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver())