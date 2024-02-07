package dsl

expect class AssetLoader {
    fun loadAsset(path: String): ByteArray
}