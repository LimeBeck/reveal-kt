package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes
import kotlinx.html.div

data class Column(
    override val id: ID = UuidGenerator.generateId(),
    val autoanimate: Boolean,
    val elements: List<RevealKtElement>
) : RevealKtElement {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        div {
            classes = classes + "col"
            attributes["data-id"] = this@Column.id.id
            if (autoanimate) attributes["data-auto-animate"] = ""
            for (element in elements) {
                element.render(this)
            }
        }
    }
}