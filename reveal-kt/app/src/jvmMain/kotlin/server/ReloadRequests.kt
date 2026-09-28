package dev.limebeck.application.server

import dev.limebeck.application.filesWatcher.UpdatedFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.OVERFLOW
import kotlin.io.path.Path

/**
 * Turns file system events into reload requests. Events are filtered before they are conflated,
 * so an unrelated change can never replace a pending change to the script or its assets.
 */
internal class ReloadRequests(private val scriptPath: Path, private val assetsPath: Path) {
    private val requests = Channel<Unit>(Channel.CONFLATED)

    val signals: Flow<Unit> = requests.receiveAsFlow()

    /** Files the last successful render read, such as `codeFromFile` sources. */
    @Volatile
    var dependencies: Set<Path> = emptySet()

    fun offer(events: List<UpdatedFile>) {
        if (events.any(::isRelevant)) requests.trySend(Unit)
    }

    private fun isRelevant(event: UpdatedFile): Boolean {
        val changed = Path(event.path)
        return event.type == OVERFLOW || changed == scriptPath || changed.startsWith(assetsPath) ||
            changed in dependencies
    }
}
