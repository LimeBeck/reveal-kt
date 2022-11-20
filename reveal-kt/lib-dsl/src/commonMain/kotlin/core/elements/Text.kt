package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.span
import utils.s


data class RegularText(override val id: ID = UuidGenerator.generateId(), val text: String) : RevealKtElement {
    constructor(id: ID = UuidGenerator.generateId(), titleProvider: () -> String) : this(id, titleProvider())

    override fun render(tag: HtmlBlockTag) = with(tag) {
        span {
            attributes["data-id"] = this@RegularText.id.id
            +this@RegularText.text
        }
    }
}

data class StrikethroughtText(
    override val id: ID = UuidGenerator.generateId(),
    val text: String,
) : RevealKtElement {
    constructor(
        id: ID = UuidGenerator.generateId(),
        titleProvider: () -> String,
    ) : this(id, titleProvider())

    override fun render(tag: HtmlBlockTag) = with(tag) {
        s {
            attributes["data-id"] = this@StrikethroughtText.id.id
            +this@StrikethroughtText.text
        }
    }
}