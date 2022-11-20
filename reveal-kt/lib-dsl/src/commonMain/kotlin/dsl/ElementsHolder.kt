package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.RevealKtElement

interface ElementsHolder {
    val elements: MutableList<RevealKtElement>

    operator fun RevealKtElement.unaryPlus() {
        elements.add(this)
    }
}