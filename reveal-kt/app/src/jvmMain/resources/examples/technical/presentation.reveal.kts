import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

title = "Reliable retries in Kotlin"
configuration {
    theme = RevealKt.Configuration.Theme.Predefined.BLACK
    slideNumber = RevealKt.Configuration.SlideNumber.Custom("c/t")
    pdfSeparateFragments = false
}
slides {
    regularSlide {
        +title { "Reliable retries in Kotlin" }
        +regularText { "A five-minute engineering talk: retry only what can safely happen twice." }
        +note { "Ask the audience what happens when a payment request times out after reaching the server." }
    }
    regularSlide {
        +title { "The timeout is ambiguous" }
        +unorderedListOf("The server may have completed the operation.", "Repeating a write can duplicate its effect.", "Use an idempotency key for retried writes.")
    }
    regularSlide {
        +title { "Keep the policy explicit" }
        +code(lang = "kotlin", lines = "1-6") {
            """
            fun shouldRetry(status: Int, attempt: Int): Boolean =
                attempt < 3 && status in setOf(429, 502, 503, 504)

            val retry = shouldRetry(status = 503, attempt = 1)
            println(retry) // true
            """.trimIndent()
        }
        +note { "The example models the decision only. Production code also needs backoff, deadlines and idempotency." }
    }
    regularSlide {
        +title { "Takeaways" }
        +unorderedListOf("Retry transient failures, not every exception.", "Bound attempts and total elapsed time.", "Measure exhausted retries and duplicate suppression.", fragmented = false)
        +regularText { "Discussion: which operations in your service are safe to retry?" }
    }
}
