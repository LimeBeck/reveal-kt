package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.dsl.slides.RegularSlideBuilder
import core.elements.Note
import core.elements.Title
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
