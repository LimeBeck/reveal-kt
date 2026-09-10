import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

title = "From a script to a shareable presentation"
configuration {
    theme = RevealKt.Configuration.Theme.Custom("assets/theme.css")
    slideNumber = RevealKt.Configuration.SlideNumber.Custom("c/t")
    pdfSeparateFragments = false
}
slides {
    regularSlide {
        +title { "Write. Preview. Share." }
        +regularText { "This presentation brings its own CSS theme and SVG image." }
        +note { "The theme uses system fonts, so no font installation is required." }
    }
    regularSlide {
        +title { "One source, three outputs" }
        +img(src = "pipeline.svg") { width = 850 }
        +regularText { "Keep theme.css and pipeline.svg beside each other in assets/." }
    }
    regularSlide {
        +title { "Make it yours" }
        +unorderedListOf("Edit the accent color in assets/theme.css.", "Save while run is active to see live reload.", "Bundle the entire output directory or export a PDF.", fragmented = false)
    }
}
