package dev.limebeck.application.filesWatcher

import dev.limebeck.application.debug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlinx.coroutines.isActive
import org.slf4j.LoggerFactory
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory

val logger = LoggerFactory.getLogger("FilesWatcher")

data class UpdatedFile(
    val path: String,
    val type: WatchEvent.Kind<out Any>,
    val updateTime: Instant
)

private fun WatchService.registerRecursive(root: Path) {
    logger.debug { "<3d6824a5> Watcher register recursive path '${root.absolutePathString()}'" }
    Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
            logger.debug { "<2ce0bd91> Register path: $dir" }

            dir.register(
                this@registerRecursive,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )
            return FileVisitResult.CONTINUE
        }
    })
}

suspend fun watchFilesRecursive(path: Path, block: suspend (events: List<UpdatedFile>) -> Unit) =
    withContext(Dispatchers.IO) {
        FileSystems.getDefault().newWatchService().use { service ->
            service.registerRecursive(path.toAbsolutePath().normalize())
            while (currentCoroutineContext().isActive) {
                val key = service.poll(100, MILLISECONDS) ?: continue
                val directory = key.watchable() as Path
                val events = key.pollEvents().map { event ->
                    val changed = (event.context() as? Path)?.let { directory.resolve(it).normalize() }
                        ?: directory
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && changed.isDirectory()) {
                        try {
                            service.registerRecursive(changed)
                        } catch (_: NoSuchFileException) {
                            // The directory may have been deleted again before registration.
                        }
                    }
                    UpdatedFile(path = changed.toString(), type = event.kind(), updateTime = Instant.now())
                }
                key.reset()
                block(events)
            }
        }
    }
