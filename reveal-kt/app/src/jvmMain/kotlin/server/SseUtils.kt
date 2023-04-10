package dev.limebeck.application.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import java.io.IOException

fun String.appendSseReloadScript() = this + """
        <script type="text/javascript">
            var source = new EventSource('/sse');
            source.addEventListener('PageUpdated', function(e) {
                location.reload()
            }, false);
        </script>
""".trimIndent()

data class SseEvent(val data: String, val event: String? = null, val id: String? = null)

suspend fun ApplicationCall.respondSse(events: ReceiveChannel<SseEvent>) =
    coroutineScope {
        response.cacheControl(CacheControl.NoCache(null))
        respondTextWriter(contentType = ContentType.Text.EventStream) {
            events.consumeEach { event ->
                logger.debug("<0c08a270> Send event $event")
                try {
                    if (event.id != null) {
                        write("id: ${event.id}\n")
                    }
                    if (event.event != null) {
                        write("event: ${event.event}\n")
                    }
                    for (dataLine in event.data.lines()) {
                        write("data: $dataLine\n")
                    }
                    write("\n")
                    flush()
                } catch (e: IOException) {
                    logger.debug("<b785289> Closed socked. Cancel execution")
                    this@coroutineScope.cancel()
                }
            }
        }
    }
