package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.elements.*
import dev.limebeck.revealkt.dsl.slides.RegularSlideBuilder
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator

fun RegularSlideBuilder.title(
    id: ID = UuidGenerator.generateId(),
    block: () -> String
) = Title(id, block()).also {
    elements.add(it)
}

fun title(
    id: ID = UuidGenerator.generateId(),
    block: () -> String
) = Title(id, block())

fun title(
    id: ID = UuidGenerator.generateId(),
    title: String,
) = Title(id, title)

fun RegularSlideBuilder.title(
    title: String,
    id: ID = UuidGenerator.generateId()
) = Title(id, title).also {
    elements.add(it)
}

fun RegularSlideBuilder.note(
    id: ID = UuidGenerator.generateId(),
    block: () -> String
) = Note(id, block()).also {
    elements.add(it)
}

fun RegularSlideBuilder.note(
    note: String,
    id: ID = UuidGenerator.generateId()
) = Note(id, note).also {
    elements.add(it)
}

fun RegularSlideBuilder.regularText(
    text: String,
    id: ID = UuidGenerator.generateId()
) = RegularText(id, text).also {
    elements.add(it)
}

fun RegularSlideBuilder.regularText(
    id: ID = UuidGenerator.generateId(),
    block: () -> String
) = RegularText(id, block()).also {
    elements.add(it)
}

fun RegularSlideBuilder.code(
    code: String,
    id: ID = UuidGenerator.generateId()
) = Code(id, code).also {
    elements.add(it)
}

fun RegularSlideBuilder.code(
    id: ID = UuidGenerator.generateId(),
    block: () -> String
) = Code(id, block()).also {
    elements.add(it)
}

class ImageConfigurationBuilder {
    var height: Int? = null
    var width: Int? = null

    fun build(): Image.Configuration = Image.Configuration(height, width)
}

fun RegularSlideBuilder.img(
    id: ID = UuidGenerator.generateId(),
    src: String,
    configurationBlock: (ImageConfigurationBuilder.() -> Unit)? = null
) = Image(
    id = id,
    path = src,
    configuration = ImageConfigurationBuilder().apply { if (configurationBlock != null) configurationBlock() }.build()
).also {
    elements.add(it)
}