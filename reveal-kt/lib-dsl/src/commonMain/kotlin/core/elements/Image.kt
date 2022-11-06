package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes
import kotlinx.html.img


data class Image(
    override val id: ID = UuidGenerator.generateId(),
    val path: String,
    val configuration: Configuration = Configuration()
) : RevealKtElement {
    data class Configuration(
        val height: Int? = null,
        val width: Int? = null,
        val stretch: Boolean = true
    )

    override fun render(tag: HtmlBlockTag) = with(tag) {
        img {
            attributes["data-id"] = this@Image.id.id

            if (configuration.stretch) {
                classes = classes + "r-stretch"
            }

            src = "assets/$path"
            configuration.height?.let {
                height = it.toString()
            }
            configuration.width?.let {
                width = it.toString()
            }
        }
    }
}