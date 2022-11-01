package dev.limebeck.revealkt.utils

import kotlin.jvm.JvmInline

@JvmInline
value class ID(val id: String) {
    override fun toString(): String = id
}