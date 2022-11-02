package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.elements.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import kotlinx.html.*


data class Code(override val id: ID = UuidGenerator.generateId(), val code: String) : RevealKtElement {
    constructor(id: ID = UuidGenerator.generateId(), titleProvider: () -> String) : this(id, titleProvider())

    override fun render(tag: HtmlBlockTag) = with(tag) {
        pre {
            attributes["data-id"] = "code-animation"
            code {
                style = "line-height: normal; padding: 0 3px;font-size: small; overflow-wrap:normal"
                script("text/template") {
                    unsafe {
                        +this@Code.code
                    }
                }
            }
        }
    }
}