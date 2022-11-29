import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.core.elements.*
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

title = "Hello from my awesome presentation"

configuration {
    controls = false
    progress = false
    theme = RevealKt.Configuration.Theme.Predefined.BLACK
    additionalCssStyle = """
        @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500&display=swap');

		.reveal h1,
		.reveal h2,
		.reveal h3,
		.reveal h4,
		.reveal h5,
		.reveal h6 {
			font-family: 'Roboto', sans-serif;
		}

		.reveal .slide {
			font-family: 'Roboto', sans-serif;
		}

		.container {
			display: flex;
		}

		.col {
			flex: 1;
		}
    """.trimIndent()
}

slides {
    regularSlide {
        autoanimate = true
        +title { "{basename}" }
    }
    verticalSlide {
        val title = Title { "Some text" }
        slide {
            autoanimate = true
            +title
            +note {
                "Some note"
            }
        }
        slide {
            autoanimate = true
            +title
            +title { "Updated text" }
            +note {
                "Some note"
            }
        }
        slide {
            autoanimate = true
            +title
            +code {
                //language=JSON
                """
                   {
                    "string": "some string"
                   } 
                """.trimIndent()
            }
        }
        slide {
            +img(src = "image.png") {
                stretch = true
            }
        }
    }
}