package dev.limebeck.revealkt.dsl

actual class AssetLoader(
    val assetPath: String
) {
    actual fun loadAsset(path: String): ByteArray {
        throw UnsupportedOperationException("<7a82bcd3> Loading assets is only supported on the JVM")
    }
}