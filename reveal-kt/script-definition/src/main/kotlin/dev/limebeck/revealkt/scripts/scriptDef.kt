package dev.limebeck.revealkt.scripts

import dev.limebeck.revealkt.dsl.RevealKtBuilder
import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
    fileExtension = "reveal.kts",
    compilationConfiguration = RevealKtScriptCompilationConfiguration::class,
    evaluationConfiguration = RevealKtEvaluationConfiguration::class,
)
abstract class RevealKtScript

object RevealKtScriptCompilationConfiguration : ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
    defaultImports(DependsOn::class, Repository::class)
    defaultImports(
        "dev.limebeck.revealkt.core.elements.*",
        "dev.limebeck.revealkt.dsl.*",
        "dev.limebeck.revealkt.dsl.slides.*"
    )
    implicitReceivers(RevealKtBuilder::class)
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    compilerOptions.append("-Xadd-modules=ALL-MODULE-PATH")

    // Callbacks
    refineConfiguration {
        // Process specified annotations with the provided handler
        onAnnotations(DependsOn::class, Repository::class, handler = ::configureMavenDepsOnAnnotations)
    }
})

object RevealKtEvaluationConfiguration : ScriptEvaluationConfiguration({
    scriptsInstancesSharing(false)
    implicitReceivers(RevealKtBuilder())
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