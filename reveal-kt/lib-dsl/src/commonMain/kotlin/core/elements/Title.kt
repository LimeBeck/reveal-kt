package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h1


data class Title(override val id: ID = UuidGenerator.generateId(), val title: String) : RevealKtElement {
    constructor(id: ID = UuidGenerator.generateId(), titleProvider: () -> String) : this(id, titleProvider())

    override fun render(tag: HtmlBlockTag) = with(tag) {
        h1 {
            attributes["data-id"] = this@Title.id.id
            +this@Title.title
        }
    }
}