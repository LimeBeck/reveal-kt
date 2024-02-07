package dsl

import kotlin.io.path.Path
import kotlin.io.path.readBytes

actual class AssetLoader(
    val assetPath: String
) {
    actual fun loadAsset(path: String): ByteArray = Path(assetPath, path).readBytes()
}