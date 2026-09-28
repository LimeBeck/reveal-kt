import dev.limebeck.revealkt.utils.extractSnippet
import dev.limebeck.revealkt.utils.languageForFile
import kotlin.test.*

class SnippetsTest {
    private val source = """
        package demo

        class Service {
            // region fetch
            fun fetch(id: Int): String {
                // region inner
                val key = "item-${'$'}id"
                // endregion
                return cache[key] ?: load(key)
            }
            // endregion

            # region shell
            echo hi
            # endregion
        }
    """.trimIndent()

    @Test
    fun `region selects its body without markers and indentation`() {
        assertEquals(
            """
            fun fetch(id: Int): String {
                val key = "item-${'$'}id"
                return cache[key] ?: load(key)
            }
            """.trimIndent(),
            extractSnippet(source, region = "fetch"),
        )
        assertEquals("val key = \"item-${'$'}id\"", extractSnippet(source, region = "inner"))
        assertEquals("echo hi", extractSnippet(source, region = "shell"))
    }

    @Test
    fun `range selects lines inside a region or file`() {
        assertEquals("class Service {", extractSnippet(source, range = 3..3))
        // Ranges count source lines, including nested region markers, as an editor shows them.
        assertEquals("    return cache[key] ?: load(key)\n}", extractSnippet(source, region = "fetch", range = 5..6))
    }

    @Test
    fun `missing regions and ranges explain what is wrong`() {
        val missing = assertFailsWith<IllegalArgumentException> { extractSnippet(source, region = "absent", source = "Service.kt") }
        assertEquals("Region 'absent' is not found in Service.kt", missing.message)
        assertFailsWith<IllegalArgumentException> { extractSnippet("// region open\nx", region = "open") }
        val range = assertFailsWith<IllegalArgumentException> { extractSnippet("a\nb", range = 2..5) }
        assertTrue(range.message!!.contains("2 lines"))
    }

    @Test
    fun `languages come from file extensions`() {
        assertEquals("kotlin", languageForFile("src/Main.kt"))
        assertEquals("kotlin", languageForFile("build.gradle.kts"))
        assertEquals("yaml", languageForFile("ci.YML"))
        assertNull(languageForFile("Makefile"))
    }
}
