package dev.limebeck.revealkt.utils

import com.benasher44.uuid.uuid4

interface IdGenerator {
    fun generateId(): ID
}

object UuidGenerator : IdGenerator {
    override fun generateId(): ID {
        return ID(uuid4().toString())
    }
}
