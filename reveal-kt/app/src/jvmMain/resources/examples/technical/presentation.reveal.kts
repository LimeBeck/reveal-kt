import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

title = "RevealKt: Kotlin DSL and code"
configuration {
    theme = RevealKt.Configuration.Theme.Custom("assets/technical.css")
    slideNumber = RevealKt.Configuration.SlideNumber.Custom("c/t")
    pdfSeparateFragments = false
}
slides {
    regularSlide {
        +title { "A presentation is Kotlin" }
        +code(lang = "kotlin") {
            """
            slides {
                regularSlide {
                    +title { "Hello, Kotlin" }
                    +regularText { "A real Reveal.js slide." }
                }
            }
            """.trimIndent()
        }
        +regularText { "RevealKt builds the HTML. Reveal.js handles navigation, layout and interaction. Press Right to explore." }
    }
    regularSlide {
        +title { "Walk through the code" }
        +code(lang = "kotlin", lines = "1|2|3") {
            """
            val name: String? = null
            val length = name?.length ?: 0
            println(length)
            """.trimIndent()
        }
        +regularText { "Press Right twice: the highlighted line advances before the next slide. DSL: code(lang = \"kotlin\", lines = \"1|2|3\")." }
    }
    regularSlide {
        +title { "Compose a two-column layout" }
        +row {
            column {
                +mediumTitle { "The DSL" }
                +code(lang = "kotlin") {
                    """
                    +row {
                        column {
                            +regularText { "Left" }
                        }
                        column {
                            +regularText { "Right" }
                        }
                    }
                    """.trimIndent()
                }
            }
            column {
                +mediumTitle { "The result" }
                +regularText { "These are actual row and column elements. Put text, code or images side by side." }
            }
        }
    }
    // One template produces two slides. Loops and data are ordinary Kotlin.
    listOf("Functions" to "Extract repeated content into reusable Kotlin functions.",
           "Data" to "Generate slides from lists, models or calculated values.").forEachIndexed { index, (heading, explanation) ->
        regularSlide {
            +title { "${index + 1}. $heading" }
            +regularText { explanation }
            +code(lang = "kotlin") {
                """
                topics.forEach { topic ->
                    regularSlide {
                        +title { topic.heading }
                        +regularText { topic.explanation }
                    }
                }
                """.trimIndent()
            }
            +regularText { "This slide and its neighbor come from the same loop in the source. Open View Kotlin source on the documentation page." }
        }
    }
    regularSlide {
        +title { "Notes for the presenter" }
        +code(lang = "kotlin") { "+note { \"Pause here and ask the audience a question.\" }" }
        +regularText { "Open this presentation in its own tab, then press S for speaker view. Allow the popup to see the note and presentation timer." }
        +note { "You found the speaker note! Ask: which parts of your next presentation could be generated from Kotlin data?" }
    }
}
