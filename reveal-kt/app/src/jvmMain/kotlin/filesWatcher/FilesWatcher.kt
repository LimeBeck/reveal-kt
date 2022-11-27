package dev.limebeck.application.filesWatcher

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

private fun registerRecursive(root: Path, watchService: WatchService) {
    logger.debug("<3d6824a5> Watcher register recursive path '${root.absolutePathString()}'")
    Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
            logger.debug("<2ce0bd91> Register path: $dir")
            dir.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )
            return FileVisitResult.CONTINUE
        }
    })
}

fun watchFilesRecursive(path: String, block: (events: List<UpdatedFile>) -> Unit) {
    val watchService = FileSystems.getDefault().newWatchService()
    val templatePath = Paths.get(path)
    registerRecursive(templatePath, watchService)

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