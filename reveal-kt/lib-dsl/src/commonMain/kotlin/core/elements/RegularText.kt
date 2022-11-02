package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.elements.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h1
import kotlinx.html.p


data class RegularText(override val id: ID = UuidGenerator.generateId(), val text: String) : RevealKtElement {
    constructor(id: ID = UuidGenerator.generateId(), titleProvider: () -> String) : this(id, titleProvider())

    override fun render(tag: HtmlBlockTag) = with(tag) {
        p { +this@RegularText.text }
    }
}