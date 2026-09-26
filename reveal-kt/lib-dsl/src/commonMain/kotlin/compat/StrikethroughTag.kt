@file:Suppress("PackageDirectoryMismatch")

package utils

import dev.limebeck.revealkt.utils.s as strikethrough
import kotlinx.html.HtmlBlockTag

@Deprecated("Moved to dev.limebeck.revealkt.utils", ReplaceWith("dev.limebeck.revealkt.utils.StrikethroughTag"))
typealias StrikethroughTag = dev.limebeck.revealkt.utils.StrikethroughTag

@Deprecated("Moved to dev.limebeck.revealkt.utils", ReplaceWith("s(block)", "dev.limebeck.revealkt.utils.s"))
fun HtmlBlockTag.s(block: dev.limebeck.revealkt.utils.StrikethroughTag.() -> Unit) = strikethrough(block)
