package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag

data class HtmlDslElement(
    override val id: ID = UuidGenerator.generateId(),
    val builder: HtmlBlockTag.() -> Unit,
) : RevealKtElement {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        builder()
    }
}