import dev.limebeck.revealkt.core.elements.*
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

title = "Hello from my presentation"

configuration {
    controls = false
    progress = false
}

slides {
    regularSlide {
        autoanimate = true
        title { "Test 3" }
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
        slide {
            autoanimate = true
            +title
            code {
                //language=JSON
                """
                   {
                    "string": "some string"
                   } 
                """.trimIndent()
            }
        }
        slide {
            img(src = "image.png")
        }
    }
}