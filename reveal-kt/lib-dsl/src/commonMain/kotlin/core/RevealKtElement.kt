package dev.limebeck.revealkt.elements

import dev.limebeck.revealkt.utils.ID
import kotlinx.html.HtmlBlockTag

interface RevealKtElement {
    val id: ID
    fun render(tag: HtmlBlockTag)
}