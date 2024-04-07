package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.*
import kotlinx.html.*


data class Code(
    override val id: ID = UuidGenerator.generateId(),
    val trim: Boolean = true,
    val lang: String? = null,
    val lines: String? = null,
    val code: String,
) : RevealKtElement {
    constructor(
        id: ID = UuidGenerator.generateId(),
        trim: Boolean = false,
        lang: String? = null,
        lines: String? = null,
        codeProvider: () -> String,
    ) : this(id, trim, lang, lines, codeProvider())

    override fun render(tag: HtmlBlockTag) = with(tag) {
        pre {
            if (this@Code.lang != null) {
                addClass(this@Code.lang)
            }
            addAttribute("data-id", this@Code.id.id)

            code {
                conditionalAttribute("data-trim", ::trim)
                conditionalAttribute("data-line-numbers", lines ?: "") { lines != null }

                script("text/template") {
                    unsafe {
                        +this@Code.code
                    }
                }
            }
        }
    }
}