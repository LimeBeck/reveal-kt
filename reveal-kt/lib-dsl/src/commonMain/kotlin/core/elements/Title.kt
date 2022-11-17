package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes
import kotlinx.html.h1


data class Title(
    override val id: ID = UuidGenerator.generateId(),
    val fitText: Boolean = false,
    val title: String,
) : RevealKtElement {
    constructor(
        id: ID = UuidGenerator.generateId(),
        fitText: Boolean = false,
        titleProvider: () -> String,
    ) : this(
        id,
        fitText,
        titleProvider()
    )

    override fun render(tag: HtmlBlockTag) = with(tag) {
        h1 {
            attributes["data-id"] = this@Title.id.id
            if(fitText){
                classes = classes + "r-fit-text"
            }
            +this@Title.title
        }
    }
}