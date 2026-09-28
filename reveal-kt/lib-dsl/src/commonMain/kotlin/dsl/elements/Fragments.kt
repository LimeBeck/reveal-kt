package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.core.elements.Fragment
import dev.limebeck.revealkt.core.elements.ListItem
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator

@RevealKtMarker
class FragmentBuilder : ElementsHolder {
    override val elements = mutableListOf<RevealKtElement>()
}

/** Shows the elements added in [block] as one step. */
fun fragment(
    effect: ListItem.Effect = ListItem.Effect.NOTHING,
    index: Int? = null,
    id: ID = UuidGenerator.generateId(),
    block: FragmentBuilder.() -> Unit,
) = Fragment(id, effect, index, FragmentBuilder().apply(block).elements)

/** Shows this element as a separate step. */
fun RevealKtElement.asFragment(
    effect: ListItem.Effect = ListItem.Effect.NOTHING,
    index: Int? = null,
) = Fragment(effect = effect, index = index, elements = listOf(this))
