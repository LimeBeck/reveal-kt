package dev.limebeck.revealkt.scripts

import java.nio.file.Files
import kotlin.io.path.writeBytes
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.dependencies
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CompilationCacheTest {
    @Test
    fun `cache key changes when a dependency is replaced at the same path`() {
        val jar = Files.createTempFile("revealkt", ".jar")
        jar.writeBytes(byteArrayOf(1))
        val configuration = ScriptCompilationConfiguration {
            dependencies(JvmDependency(jar.toFile()))
        }
        val script = "title = \"Demo\"".toScriptSource()

        val before = compiledScriptUniqueName(script, configuration)
        assertEquals(before, compiledScriptUniqueName(script, configuration))

        jar.writeBytes(byteArrayOf(1, 2, 3))
        jar.toFile().setLastModified(jar.toFile().lastModified() + 10_000)
        assertNotEquals(before, compiledScriptUniqueName(script, configuration))
    }
}
