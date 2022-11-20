package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.AttributedElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h1


data class Title(
    override val id: ID = UuidGenerator.generateId(),
    val fitText: Boolean = false,
    val title: String,
) : AttributedElement(id) {
    constructor(
        id: ID = UuidGenerator.generateId(),
        fitText: Boolean = false,
        titleProvider: () -> String,
    ) : this(
        id,
        fitText,
        titleProvider()
    )

    init {
        conditionalClass("r-fit-text", ::fitText)
    }

    override fun HtmlBlockTag.tagBodyProvider(renderAttributes: HtmlBlockTag.() -> Unit) {
        h1 {
            renderAttributes()
            +this@Title.title
        }
    }
}