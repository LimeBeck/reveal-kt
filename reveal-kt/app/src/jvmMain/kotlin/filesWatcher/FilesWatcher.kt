package dev.limebeck.application.filesWatcher

import dev.limebeck.application.debug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import kotlin.io.path.absolutePathString

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
        val watchService = FileSystems
            .getDefault()
            .newWatchService()
            .apply {
                registerRecursive(path)
            }

        var key: WatchKey
        while (watchService.take().also { key = it } != null) {
            block(
                key.pollEvents().map { watchEvent ->

                    UpdatedFile(
                        path = watchEvent.context().toString(),
                        type = watchEvent.kind(),
                        updateTime = Instant.now()
                    )
                }
            )
            key.reset()
        }
    }