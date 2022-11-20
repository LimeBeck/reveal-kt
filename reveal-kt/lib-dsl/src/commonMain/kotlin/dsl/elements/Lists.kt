package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.core.elements.ListItem
import dev.limebeck.revealkt.core.elements.OrderedList
import dev.limebeck.revealkt.core.elements.RegularText
import dev.limebeck.revealkt.core.elements.UnorderedList
import kotlin.jvm.JvmName

@JvmName("unorderedListOfStrings")
fun unorderedListOf(
    list: List<String>,
    fragmented: Boolean = true,
    effect: ListItem.Effect = ListItem.Effect.FADE_UP,
): UnorderedList = UnorderedList(
    elements = list.mapIndexed { index, string ->
        ListItem(
            fragmented = fragmented && index != 0,
            effect = effect,
            element = RegularText { string }
        )
    }
)

@JvmName("unorderedListOfElements")
fun unorderedListOf(
    list: List<RevealKtElement>,
    fragmented: Boolean = true,
    effect: ListItem.Effect = ListItem.Effect.FADE_UP,
): UnorderedList = UnorderedList(
    elements = list.mapIndexed { index, element ->
        ListItem(
            fragmented = fragmented && index != 0,
            effect = effect,
            element = element
        )
    }
)

@JvmName("orderedListOfStrings")
fun orderedListOf(
    list: List<String>,
    fragmented: Boolean = true,
    effect: ListItem.Effect = ListItem.Effect.FADE_UP,
): OrderedList = OrderedList(
    elements = list.mapIndexed { index, string ->
        ListItem(
            fragmented = fragmented && index != 0,
            effect = effect,
            element = RegularText { string }
        )
    }
)

@JvmName("orderedListOfElements")
fun orderedListOf(
    list: List<RevealKtElement>,
    fragmented: Boolean = true,
    effect: ListItem.Effect = ListItem.Effect.FADE_UP,
): OrderedList = OrderedList(
    elements = list.mapIndexed { index, element ->
        ListItem(
            fragmented = fragmented && index != 0,
            effect = effect,
            element = element
        )
    }
)