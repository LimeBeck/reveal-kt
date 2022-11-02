import dev.limebeck.revealkt.core.elements.Title
import dev.limebeck.revealkt.dsl.note
import dev.limebeck.revealkt.dsl.slides.regularSlide
import dev.limebeck.revealkt.dsl.slides.slide
import dev.limebeck.revealkt.dsl.slides.verticalSlide
import dev.limebeck.revealkt.dsl.title

title = "Hello from my presentation"

configuration {
    controls = false
    progress = false
}

slides {
    regularSlide {
        autoanimate = true
        title { "Test 2" }
    }
    verticalSlide {
        val title = Title { "Some text" }
        slide {
            autoanimate = true
            +title
            note {
                "Some note"
            }
        }
        slide {
            autoanimate = true
            +title
            title { "Updated text" }
            note {
                "Some note"
            }
        }
    }
}