import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

title = "{{basename}}"
configuration {
    theme = RevealKt.Configuration.Theme.Predefined.BLACK
    pdfSeparateFragments = false
}
slides {
    regularSlide {
        +title { "My first presentation" }
        +regularText { "Edit this text and save to see live reload." }
        +note { "Press S in the browser to open speaker notes." }
    }
    regularSlide {
        +title { "Add an image" }
        +img(src = "image.png") { height = 350 }
        +regularText { "Images live in the assets directory beside this script." }
    }
}
