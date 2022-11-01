package core.elements

import dev.limebeck.revealkt.elements.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.aside

data class Note(override val id: ID = UuidGenerator.generateId(), val note: String) : RevealKtElement {
    constructor(id: ID = UuidGenerator.generateId(), noteProvider: () -> String) : this(id, noteProvider())

    override fun render(tag: HtmlBlockTag) = with(tag) {
        aside(classes = "notes") { +note }
    }
}