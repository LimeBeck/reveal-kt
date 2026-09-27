import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*
import dev.limebeck.revealkt.utils.UuidGenerator

title = "RevealKt: navigation and motion"
configuration {
    theme = RevealKt.Configuration.Theme.Custom("assets/lesson.css")
    slideNumber = RevealKt.Configuration.SlideNumber.Custom("c/t")
    pdfSeparateFragments = false
}
val movingLabel = UuidGenerator.generateId()
slides {
    regularSlide {
        +title { "Let the audience set the pace" }
        +regularText { "Try fragments, vertical navigation and Auto-Animate. These are Reveal.js features driven by the Kotlin DSL." }
        +unorderedListOf("Right / Space: advance", "Down: explore a vertical stack", "Esc: open the slide overview", fragmented = false)
    }
    regularSlide {
        +title { "Reveal one idea at a time" }
        +orderedListOf(listOf("The first item is visible immediately.", "Press Right: Reveal.js reveals this fragment.", "Press Right again: one more idea, on the same slide."), fragmented = true)
        +code(lang = "kotlin") { "+orderedListOf(points, fragmented = true)" }
        +note { "Use Left to reverse a fragment. pdfSeparateFragments = false combines all steps on one PDF page." }
    }
    verticalSlide {
        regularSlide {
            +title { "Go deeper, not further" }
            +regularText { "This slide begins a vertical stack. Press Down for a detail slide, or Right to skip to the next topic." }
            +code(lang = "kotlin") {
                """
                verticalSlide {
                    regularSlide { /* overview */ }
                    regularSlide { /* details */ }
                }
                """.trimIndent()
            }
        }
        regularSlide {
            +title { "You are inside the stack" }
            +regularText { "Press Up to return. Press Esc to see the two-dimensional slide map, then Esc again to resume." }
            +code(lang = "kotlin") { "configuration { overview = true }" }
            +note { "Vertical slides let a presenter keep optional details underneath a main topic." }
        }
    }
    regularSlide {
        +title { "One element, two layouts" }
        +regularText(id = movingLabel) { "Follow this element →" }
        +code(lang = "kotlin") {
            """
            val movingLabel = UuidGenerator.generateId()
            // Reuse the ID on adjacent slides:
            +regularText(id = movingLabel) { "Follow this element →" }
            """.trimIndent()
        }
        +regularText { "Press Right. The next slide reuses the element ID; Reveal.js animates its position and size." }
    }
    regularSlide {
        +title { "Auto-Animate connects the dots" }
        +code(lang = "kotlin") { "// Same element ID, new position and CSS." }
        +regularText(id = movingLabel) { "Follow this element →" }
        +regularText { "Press Left to reverse the transition. Regular slides enable autoanimate by default; set autoanimate = false to opt out." }
    }
}
