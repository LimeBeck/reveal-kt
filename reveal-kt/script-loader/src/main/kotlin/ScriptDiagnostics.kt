package dev.limebeck.revealkt.scripts

import java.io.File
import kotlin.script.experimental.api.ScriptDiagnostic

fun RevealKtScriptLoader.LoadResult.Error.formatDiagnostics(script: File): String {
    val errors = diagnostic.filter {
        it.severity == ScriptDiagnostic.Severity.ERROR || it.severity == ScriptDiagnostic.Severity.FATAL
    }.ifEmpty { diagnostic }
    return errors.joinToString("\n") { report ->
        buildString {
            append(report.sourcePath ?: script.absoluteFile.normalize().path)
            report.location?.start?.let { position ->
                append(":${position.line}")
                // A runtime stack frame supplies a line, but no column.
                if (report.exception == null) append(":${position.col}")
            }
            append(": ${report.severity.name.lowercase()}: ${report.message}")
        }
    }.ifEmpty { "${script.absolutePath}: error: Script did not produce a presentation." }
}
