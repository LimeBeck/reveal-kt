package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes
import kotlinx.html.div

/**
 * Reveals [elements] step by step, see https://revealjs.com/fragments/.
 * Fragments with the same [index] appear together; lower indexes appear first.
 */
data class Fragment(
    override val id: ID = UuidGenerator.generateId(),
    val effect: ListItem.Effect = ListItem.Effect.NOTHING,
    val index: Int? = null,
    val elements: List<RevealKtElement>,
) : RevealKtElement {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        div {
            attributes["data-id"] = this@Fragment.id.id
            classes = setOfNotNull("fragment", effect.value.ifEmpty { null })
            index?.let { attributes["data-fragment-index"] = it.toString() }
            for (element in elements) {
                element.render(this)
            }
        }
    }
}
