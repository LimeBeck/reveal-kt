package dev.limebeck.revealkt.core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.*
import kotlinx.html.*


data class Code(
    override val id: ID = UuidGenerator.generateId(),
    val trim: Boolean = true,
    val lang: String? = null,
    val lines: String = "",
    val code: String,
) : RevealKtElement {
    constructor(
        id: ID = UuidGenerator.generateId(),
        trim: Boolean = true,
        lang: String? = null,
        lines: String = "",
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
                conditionalAttribute("data-line-numbers", lines) { lines.isNotBlank() && lines.isNotEmpty() }

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