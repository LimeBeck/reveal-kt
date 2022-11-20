package utils

import kotlinx.html.*

class StrikethroughTag(consumer: TagConsumer<*>) : HTMLTag(
    tagName = "s",
    consumer = consumer,
    initialAttributes = emptyMap(),
    inlineTag = true,
    emptyTag = false
), HtmlInlineTag

fun HtmlBlockTag.s(block: StrikethroughTag.() -> Unit) {
    StrikethroughTag(consumer).visit(block)
}