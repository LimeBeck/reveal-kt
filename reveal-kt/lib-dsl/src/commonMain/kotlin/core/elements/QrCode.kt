package core.elements

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.utils.ID
import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes
import kotlinx.html.img
import qrcode.QRCodeBuilder
import qrcode.color.Colors
import qrcode.render.QRCodeGraphicsFactory
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class QrCode(
    override val id: ID,
    val value: String,
    val configuration: Configuration = Configuration(),
) : RevealKtElement {
    data class Configuration(
        val height: Int? = null,
        val width: Int? = null,
        val stretch: Boolean = true,
        val transformBuilder: ((builder: QRCodeBuilder) -> QRCodeBuilder)? = null
    )

    @OptIn(ExperimentalEncodingApi::class)
    override fun render(tag: HtmlBlockTag) = with(tag) {
        val builder = qrcode.QRCode
            .ofCircles()
            .withColor(Colors.BLUE)
            .let { b ->
                configuration.transformBuilder?.let { it(b) } ?: b
            }

        val qrCode = builder.build(value)

        img {
            attributes["data-id"] = this@QrCode.id.id

            if (configuration.stretch) {
                classes = classes + "r-stretch"
            }

            src = "data:image/png;base64, " + Base64.encode(qrCode.renderToBytes())
            configuration.height?.let {
                height = it.toString()
            }
            configuration.width?.let {
                width = it.toString()
            }
        }
    }
}