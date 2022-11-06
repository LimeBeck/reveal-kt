val Reveal = kotlinext.js.require("reveal.js/dist/reveal.js")
external val configuration: dynamic

val defaultPlugins = arrayOf(
    kotlinext.js.require("reveal.js/plugin/notes/notes.js"),
    kotlinext.js.require("reveal.js/plugin/highlight/highlight.js"),
    kotlinext.js.require("reveal.js/plugin/markdown/markdown.js"),
    kotlinext.js.require("reveal.js/plugin/search/search.js"),
    kotlinext.js.require("reveal.js/plugin/zoom/zoom.js"),
    kotlinext.js.require("reveal.js/plugin/math/math.js"),
)

fun main() {
    kotlinext.js.require("reveal.js/dist/reset.css")
    kotlinext.js.require("reveal.js/dist/reveal.css")
    when (configuration.theme) {
        "beige" -> kotlinext.js.require("reveal.js/dist/theme/beige.css")
        "black" -> kotlinext.js.require("reveal.js/dist/theme/black.css")
        "blood" -> kotlinext.js.require("reveal.js/dist/theme/blood.css")
        "league" -> kotlinext.js.require("reveal.js/dist/theme/league.css")
        "moon" -> kotlinext.js.require("reveal.js/dist/theme/moon.css")
        "night" -> kotlinext.js.require("reveal.js/dist/theme/night.css")
        "serif" -> kotlinext.js.require("reveal.js/dist/theme/serif.css")
        "simple" -> kotlinext.js.require("reveal.js/dist/theme/simple.css")
        "sky" -> kotlinext.js.require("reveal.js/dist/theme/sky.css")
        "solarized" -> kotlinext.js.require("reveal.js/dist/theme/solarized.css")
        "white" -> kotlinext.js.require("reveal.js/dist/theme/white.css")
        else -> kotlinext.js.require(configuration.theme as String)
    }

    kotlinext.js.require("reveal.js/plugin/highlight/monokai.css")

    configuration.plugins = defaultPlugins
//    console.log(configuration)
    Reveal(
        configuration
    ).initialize()
}

