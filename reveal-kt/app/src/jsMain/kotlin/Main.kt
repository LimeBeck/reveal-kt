import dev.limebeck.revealkt.server.ConfigurationDto
import dev.limebeck.revealkt.server.configurationJsomMapper
import kotlinx.browser.document
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromDynamic
import kotlinx.serialization.json.encodeToDynamic

val Reveal = kotlinext.js.require("reveal.js/dist/reveal.js")
external val configurationJson: dynamic

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val configuration = configurationJsomMapper.decodeFromDynamic<ConfigurationDto>(configurationJson)

    val defaultPlugins = arrayOf(
        kotlinext.js.require("reveal.js/plugin/notes/notes.js"),
        kotlinext.js.require("reveal.js/plugin/highlight/highlight.js"),
        kotlinext.js.require("reveal.js/plugin/markdown/markdown.js"),
        kotlinext.js.require("reveal.js/plugin/search/search.js"),
        kotlinext.js.require("reveal.js/plugin/zoom/zoom.js"),
        kotlinext.js.require("reveal.js/plugin/math/math.js"),
    )

    kotlinext.js.require("reveal.js/dist/reset.css")
    kotlinext.js.require("reveal.js/dist/reveal.css")
    when (configuration.theme) {
        is ConfigurationDto.Theme.Predefined -> {
            when (configuration.theme) {
                ConfigurationDto.Theme.Predefined.BEIGE -> kotlinext.js.require("reveal.js/dist/theme/beige.css")
                ConfigurationDto.Theme.Predefined.BLACK -> kotlinext.js.require("reveal.js/dist/theme/black.css")
                ConfigurationDto.Theme.Predefined.BLOOD -> kotlinext.js.require("reveal.js/dist/theme/blood.css")
                ConfigurationDto.Theme.Predefined.LEAGUE -> kotlinext.js.require("reveal.js/dist/theme/league.css")
                ConfigurationDto.Theme.Predefined.MOON -> kotlinext.js.require("reveal.js/dist/theme/moon.css")
                ConfigurationDto.Theme.Predefined.NIGHT -> kotlinext.js.require("reveal.js/dist/theme/night.css")
                ConfigurationDto.Theme.Predefined.SERIF -> kotlinext.js.require("reveal.js/dist/theme/serif.css")
                ConfigurationDto.Theme.Predefined.SIMPLE -> kotlinext.js.require("reveal.js/dist/theme/simple.css")
                ConfigurationDto.Theme.Predefined.SKY -> kotlinext.js.require("reveal.js/dist/theme/sky.css")
                ConfigurationDto.Theme.Predefined.SOLARIZED -> kotlinext.js.require("reveal.js/dist/theme/solarized.css")
                ConfigurationDto.Theme.Predefined.WHITE -> kotlinext.js.require("reveal.js/dist/theme/white.css")
                ConfigurationDto.Theme.Predefined.DRACULA -> kotlinext.js.require("reveal.js/dist/theme/dracula.css")
            }
        }

        is ConfigurationDto.Theme.Custom -> kotlinext.js.require(configuration.theme.cssLink)
    }

    kotlinext.js.require("reveal.js/plugin/highlight/monokai.css")

    val dynamicConfiguration = configurationJsomMapper.encodeToDynamic(configuration)
    dynamicConfiguration.plugins = defaultPlugins
    Reveal(
        dynamicConfiguration
    ).initialize()

    configuration.additionalCssStyle?.let { it: String ->
        val style = document.createElement("style").apply {
            textContent = it
        }
        document.head?.appendChild(style)
    }
}

