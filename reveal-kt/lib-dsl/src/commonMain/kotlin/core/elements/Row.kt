package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes
import kotlinx.html.div

data class Row(
    override val id: ID = UuidGenerator.generateId(),
    val columns: List<Column>
) : RevealKtElement {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        div {
            attributes["data-id"] = this@Row.id.id
            classes = classes + "container"
            for (column in columns) {
                column.render(this)
            }
        }
    }
}