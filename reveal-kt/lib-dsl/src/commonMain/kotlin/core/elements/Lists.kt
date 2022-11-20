package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.*

class UnorderedList(
    override val id: ID = UuidGenerator.generateId(),
    val elements: List<ListItem>,
) : RevealKtElement {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        ul {
            elements.forEachIndexed { index, revealKtElement ->
                revealKtElement.render(this@ul)
            }
        }
    }
}

class OrderedList(
    override val id: ID = UuidGenerator.generateId(),
    val elements: List<ListItem>,
) : RevealKtElement {
    override fun render(tag: HtmlBlockTag) = with(tag) {
        ol {
            elements.forEachIndexed { index, revealKtElement ->
                revealKtElement.render(this@ol)
            }
        }
    }
}

class ListItem(
    val fragmented: Boolean,
    val effect: Effect = Effect.NOTHING,
    val element: RevealKtElement,
) {
    sealed class Effect(open val value: String) {

        object NOTHING : Effect("")
        object FADE_UP : Effect("fade-up")
        object FADE_DOWN : Effect("fade-down")
        object FADE_OUT : Effect("fade-out")
        object FADE_LEFT : Effect("fade-left")
        object FADE_RIGHT : Effect("fade-right")
        object FADE_IN_THEN_OUT : Effect("fade-in-then-out")
        object FADE_IN_THEN_SEMI_OUT : Effect("fade-in-then-semi-out")
        object GROW : Effect("grow")
        object SEMI_FADE_OUT : Effect("semi-fade-out")
        object SHRINK : Effect("shrink")
        object STRIKE : Effect("strike")
        object HIGHLIGHT_RED : Effect("highlight-red")
        object HIGHLIGHT_GREEN : Effect("highlight-green")
        object HIGHLIGHT_BLUE : Effect("highlight-blue")
        object HIGHLIGHT_CURRENT_RED : Effect("highlight-current-red")
        object HIGHLIGHT_CURRENT_GREEN : Effect("highlight-current-green")
        object HIGHLIGHT_CURRENT_BLUE : Effect("highlight-current-blue")

        data class Custom(override val value: String) : Effect(value)
    }

    fun LI.render() {
        if (fragmented) {
            classes = classes + setOf("fragment", effect.value)
        }
        element.render(this)
    }

    fun render(tag: UL) = with(tag) {
        li {
            render()
        }
    }

    fun render(tag: OL) = with(tag) {
        li {
            render()
        }
    }
}