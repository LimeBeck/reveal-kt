package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.elements.*
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator

fun code(
    code: String,
    lang: String? = null,
    lines: String = "",
    trim: Boolean = true,
    id: ID = UuidGenerator.generateId(),
) = Code(id, trim, lang, lines, code)

fun code(
    id: ID = UuidGenerator.generateId(),
    lang: String? = null,
    lines: String = "",
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
    id: ID = UuidGenerator.generateId(),
    src: String,
    configurationBlock: (ImageConfigurationBuilder.() -> Unit)? = null,
) = Image(
    id = id,
    path = src,
    configuration = ImageConfigurationBuilder().apply { if (configurationBlock != null) configurationBlock() }.build()
)

fun row(
    builder: RowBuilder.() -> Unit,
): Row {
    return RowBuilder()
        .apply(builder)
        .build()
}