import dev.limebeck.revealkt.server.ConfigurationDto
import dev.limebeck.revealkt.server.configurationJsonMapper
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromDynamic
import kotlinx.serialization.json.encodeToDynamic
import kotlin.js.Promise
import org.w3c.dom.Element

@JsModule("reveal.js")
external class Reveal(configuration: dynamic) {
    fun initialize(): Promise<Any?>
    fun on(event: String, listener: () -> Unit)
}

external val configurationJson: dynamic

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val configuration = configurationJsonMapper.decodeFromDynamic<ConfigurationDto>(configurationJson)

    val defaultPlugins = mutableListOf<dynamic>(
        kotlinext.js.require<dynamic>("reveal.js/plugin/notes"),
        kotlinext.js.require<dynamic>("reveal.js/plugin/highlight"),
        kotlinext.js.require<dynamic>("reveal.js/plugin/markdown"),
        kotlinext.js.require<dynamic>("reveal.js/plugin/search"),
        kotlinext.js.require<dynamic>("reveal.js/plugin/zoom"),
    ).apply {
        if (requiresMathRendering()) add(kotlinext.js.require<dynamic>("reveal.js/plugin/math"))
    }.toTypedArray()

    kotlinext.js.require<dynamic>("reveal.js/reset.css")
    kotlinext.js.require<dynamic>("reveal.js/reveal.css")
    when (configuration.theme) {
        is ConfigurationDto.Theme.Predefined -> {
            when (configuration.theme) {
                ConfigurationDto.Theme.Predefined.BEIGE -> kotlinext.js.require<dynamic>("reveal.js/theme/beige.css")
                ConfigurationDto.Theme.Predefined.BLACK -> kotlinext.js.require<dynamic>("reveal.js/theme/black.css")
                ConfigurationDto.Theme.Predefined.BLOOD -> kotlinext.js.require<dynamic>("reveal.js/theme/blood.css")
                ConfigurationDto.Theme.Predefined.LEAGUE -> kotlinext.js.require<dynamic>("reveal.js/theme/league.css")
                ConfigurationDto.Theme.Predefined.MOON -> kotlinext.js.require<dynamic>("reveal.js/theme/moon.css")
                ConfigurationDto.Theme.Predefined.NIGHT -> kotlinext.js.require<dynamic>("reveal.js/theme/night.css")
                ConfigurationDto.Theme.Predefined.SERIF -> kotlinext.js.require<dynamic>("reveal.js/theme/serif.css")
                ConfigurationDto.Theme.Predefined.SIMPLE -> kotlinext.js.require<dynamic>("reveal.js/theme/simple.css")
                ConfigurationDto.Theme.Predefined.SKY -> kotlinext.js.require<dynamic>("reveal.js/theme/sky.css")
                ConfigurationDto.Theme.Predefined.SOLARIZED -> kotlinext.js.require<dynamic>("reveal.js/theme/solarized.css")
                ConfigurationDto.Theme.Predefined.WHITE -> kotlinext.js.require<dynamic>("reveal.js/theme/white.css")
                ConfigurationDto.Theme.Predefined.DRACULA -> kotlinext.js.require<dynamic>("reveal.js/theme/dracula.css")
            }
        }

        is ConfigurationDto.Theme.Custom -> {
            val style = document.createElement("link").apply {
                setAttribute("rel", "stylesheet")
                setAttribute("href", configuration.theme.cssLink)
            }
            document.head?.appendChild(style)
        }
    }

    kotlinext.js.require<dynamic>("reveal.js/plugin/highlight/monokai.css")

    val dynamicConfiguration = configurationJsonMapper.encodeToDynamic(configuration)
    dynamicConfiguration.plugins = defaultPlugins
    dynamicConfiguration.slideNumber = when (configuration.slideNumber) {
        ConfigurationDto.SlideNumber.Enable -> true
        ConfigurationDto.SlideNumber.Disable -> false
        is ConfigurationDto.SlideNumber.Custom -> configuration.slideNumber.format
    }

    dynamicConfiguration.autoSlide = if (configuration.autoSlide < 0.0) false else configuration.autoSlide

    val deck = Reveal(dynamicConfiguration)
    window.asDynamic().revealKtDeck = deck
    deck.on("pdf-ready") { window.asDynamic().revealKtPdfReady = true }
    deck.initialize().then {
        window.asDynamic().revealKtReady = true
    }

    listOfNotNull(configuration.additionalCssStyle, configuration.additionalCssStyleFromBuilder).forEach { css ->
        val style = document.createElement("style").apply {
            textContent = css
        }
        document.head?.appendChild(style)
    }
}

private fun requiresMathRendering(): Boolean {
    val slides = document.querySelector(".slides")?.cloneNode(true) as? Element ?: return false
    // Markdown is expanded by a plugin after this check, so preserve math support for it.
    if (slides.querySelector(".math, script[type^='math/tex'], [data-markdown]") != null) return true
    val ignored = slides.querySelectorAll("script, noscript, style, textarea, pre, code")
    for (index in 0 until ignored.length) {
        ignored.item(index)?.let { node -> node.parentNode?.removeChild(node) }
    }
    val text = slides.textContent.orEmpty()
    return listOf("$", "\\(", "\\[", "\\begin{").any(text::contains)
}
