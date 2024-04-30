package dev.limebeck.revealkt.dsl

import core.elements.QrCode
import dev.limebeck.revealkt.core.elements.*
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator
import qrcode.QRCodeBuilder

fun code(
    code: String,
    lang: String? = null,
    lines: String? = null,
    trim: Boolean = true,
    id: ID = UuidGenerator.generateId(),
) = Code(id, trim, lang, lines, code)

fun code(
    id: ID = UuidGenerator.generateId(),
    lang: String? = null,
    lines: String? = null,
    trim: Boolean = true,
    block: () -> String,
) = Code(id, trim, lang, lines, block())

class ImageConfigurationBuilder {
    var height: Int? = null
    var width: Int? = null
    var stretch: Boolean = false

    fun build(): Image.Configuration = Image.Configuration(height, width, stretch)
}

fun img(
    src: String,
    id: ID = UuidGenerator.generateId(),
    configurationBlock: (ImageConfigurationBuilder.() -> Unit)? = null,
) = Image(
    id = id,
    path = src,
    configuration = ImageConfigurationBuilder().apply { if (configurationBlock != null) configurationBlock() }.build()
)

class QrCodeConfigurationBuilder {
    var height: Int? = null
    var width: Int? = null
    var stretch: Boolean = false
    private var _transformBuilder: ((builder: QRCodeBuilder) -> QRCodeBuilder)? = null

    fun transformBuilder(block: (builder: QRCodeBuilder) -> QRCodeBuilder) {
        _transformBuilder = block
    }

    fun build(): QrCode.Configuration = QrCode.Configuration(height, width, stretch, _transformBuilder)
}

fun qrCode(
    value: String,
    id: ID = UuidGenerator.generateId(),
    configurationBlock: (QrCodeConfigurationBuilder.() -> Unit)? = null,
) = QrCode(
    id = id,
    value = value,
    configuration = QrCodeConfigurationBuilder().apply { if (configurationBlock != null) configurationBlock() }.build()
)

fun row(
    builder: RowBuilder.() -> Unit,
): Row {
    return RowBuilder()
        .apply(builder)
        .build()
}