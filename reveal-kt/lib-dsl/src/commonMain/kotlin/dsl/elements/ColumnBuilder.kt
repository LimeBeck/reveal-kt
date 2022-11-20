package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.RevealKtElement
import dev.limebeck.revealkt.core.elements.Column
import dev.limebeck.revealkt.utils.UuidGenerator

class ColumnBuilder : ElementsHolder {
    var id = UuidGenerator.generateId()
    var autoanimate = false

    override val elements = mutableListOf<RevealKtElement>()

    fun build(): Column {
        return Column(
            id = id,
            autoanimate = autoanimate,
            elements = elements
        )
    }
}