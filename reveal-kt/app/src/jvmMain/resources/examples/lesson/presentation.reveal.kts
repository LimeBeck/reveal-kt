import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

title = "Kotlin null safety: predict, reveal, explain"
configuration {
    theme = RevealKt.Configuration.Theme.Predefined.WHITE
    slideNumber = RevealKt.Configuration.SlideNumber.Enable
    pdfSeparateFragments = false
}
slides {
    regularSlide {
        +title { "What does this print?" }
        +code(lang = "kotlin") {
            """
            val name: String? = null
            println(name?.length ?: 0)
            """.trimIndent()
        }
        +regularText { "Think first. Advance one step at a time to explain your answer." }
        +note { "Give learners 20 seconds before moving to the explanation." }
    }
    regularSlide {
        +title { "Follow the expression" }
        +orderedListOf(listOf("name contains null.", "The safe call ?. skips length and produces null.", "The Elvis operator ?: selects 0.", "The program prints 0."), fragmented = true)
        +note { "The first item is visible immediately; each subsequent item is a fragment." }
    }
    regularSlide {
        +title { "Try another input" }
        +regularText { "Change null to Kotlin. Predict the output, then run the code." }
        +unorderedListOf("Answer: 6.", "The safe call now returns the string length.", "The Elvis fallback is not evaluated.", fragmented = true)
        +note { "Ask learners to describe when a nullable value should be rejected instead of receiving a fallback." }
    }
}
