val Reveal = kotlinext.js.require("reveal.js/dist/reveal.js")
external val configuration: dynamic

val defaultPlugins = arrayOf(
    kotlinext.js.require("reveal.js/plugin/notes/notes.js"),
    kotlinext.js.require("reveal.js/plugin/highlight/highlight.js"),
    kotlinext.js.require("reveal.js/plugin/markdown/markdown.js"),
    kotlinext.js.require("reveal.js/plugin/search/search.js"),
)

fun main() {
    kotlinext.js.require("reveal.js/dist/reset.css")
    kotlinext.js.require("reveal.js/dist/reveal.css")
    kotlinext.js.require("reveal.js/dist/theme/black.css")
    kotlinext.js.require("reveal.js/plugin/highlight/monokai.css")

    configuration.plugins = defaultPlugins
    console.log(configuration)
    Reveal(
        configuration
    ).initialize()
}

