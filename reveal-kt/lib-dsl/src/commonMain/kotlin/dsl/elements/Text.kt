package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.elements.*
import dev.limebeck.revealkt.utils.ID
import dev.limebeck.revealkt.utils.UuidGenerator


fun title(
    id: ID = UuidGenerator.generateId(),
    fitText: Boolean = true,
    block: () -> String,
) = Title(id, fitText, block())

fun title(
    id: ID = UuidGenerator.generateId(),
    fitText: Boolean = true,
    title: String,
) = Title(id, fitText, title)

fun title(
    title: String,
    fitText: Boolean = true,
    id: ID = UuidGenerator.generateId(),
) = Title(id, fitText, title)

fun mediumTitle(
    id: ID = UuidGenerator.generateId(),
    fitText: Boolean = false,
    block: () -> String,
) = MediumTitle(id, fitText, block())

fun mediumTitle(
    id: ID = UuidGenerator.generateId(),
    fitText: Boolean = false,
    title: String,
) = MediumTitle(id, fitText, title)

fun mediumTitle(
    title: String,
    fitText: Boolean = false,
    id: ID = UuidGenerator.generateId(),
) = MediumTitle(id, fitText, title)

fun smallTitle(
    id: ID = UuidGenerator.generateId(),
    fitText: Boolean = false,
    block: () -> String,
) = SmallTitle(id, fitText, block())

fun smallTitle(
    id: ID = UuidGenerator.generateId(),
    fitText: Boolean = false,
    title: String,
) = SmallTitle(id, fitText, title)

fun smallTitle(
    title: String,
    fitText: Boolean = false,
    id: ID = UuidGenerator.generateId(),
) = SmallTitle(id, fitText, title)

fun note(
    id: ID = UuidGenerator.generateId(),
    block: () -> String,
) = Note(id, block())

fun note(
    note: String,
    id: ID = UuidGenerator.generateId(),
) = Note(id, note)

fun regularText(
    text: String,
    id: ID = UuidGenerator.generateId(),
) = RegularText(id, text)

fun regularText(
    id: ID = UuidGenerator.generateId(),
    block: () -> String,
) = RegularText(id, block())

fun regular(
    text: String,
    id: ID = UuidGenerator.generateId(),
) = RegularText(id, text)

fun regular(
    id: ID = UuidGenerator.generateId(),
    block: () -> String,
) = RegularText(id, block())

fun strikeText(
    text: String,
    id: ID = UuidGenerator.generateId(),
) = StrikethroughtText(id, text)

fun strikeText(
    id: ID = UuidGenerator.generateId(),
    block: () -> String,
) = StrikethroughtText(id, block())

fun strike(
    text: String,
    id: ID = UuidGenerator.generateId(),
) = StrikethroughtText(id, text)

fun strike(
    id: ID = UuidGenerator.generateId(),
    block: () -> String,
) = StrikethroughtText(id, block())