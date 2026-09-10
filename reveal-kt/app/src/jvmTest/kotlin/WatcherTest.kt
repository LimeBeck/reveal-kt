import dev.limebeck.application.filesWatcher.watchFilesRecursive
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.nio.file.Files
import kotlin.io.path.*
import kotlin.test.*

class WatcherTest {
    @Test
    fun `new nested directories preserve full event path and watcher cancels`() = runBlocking {
        val root = Files.createTempDirectory("watcher ")
        val events = Channel<String>(Channel.UNLIMITED)
        val watcher = launch(Dispatchers.IO) { watchFilesRecursive(root) { batch -> batch.forEach { events.send(it.path) } } }
        try {
            // Repeated writes also accommodate asynchronous initial registration.
            withTimeout(5000) {
                while (events.tryReceive().isFailure) {
                    root.resolve("ready").writeText(System.nanoTime().toString())
                    delay(50)
                }
            }
            val nested = root.resolve("assets/new/nested").createDirectories()
            val file = nested.resolve("asset.txt")
            withTimeout(5000) {
                var found = false
                while (!found) {
                    file.writeText(System.nanoTime().toString())
                    delay(50)
                    while (true) {
                        val event = events.tryReceive().getOrNull() ?: break
                        if (event == file.toString()) found = true
                    }
                }
            }
        } finally {
            withTimeout(2000) { watcher.cancelAndJoin() }
        }
        assertTrue(watcher.isCompleted)
    }
}
