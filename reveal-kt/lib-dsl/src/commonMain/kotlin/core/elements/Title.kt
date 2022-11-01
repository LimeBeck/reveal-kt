package core.elements

import dev.limebeck.revealkt.elements.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h1


data class Title(override val id: ID = UuidGenerator.generateId(), val title: String) : RevealKtElement {
    constructor(id: ID = UuidGenerator.generateId(), titleProvider: () -> String) : this(id, titleProvider())

    override fun render(tag: HtmlBlockTag) = with(tag) {
        h1 { +this@Title.title }
    }
}