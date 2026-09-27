package dev.limebeck.revealkt.dsl

expect class AssetLoader {
    fun loadAsset(path: String): ByteArray
}