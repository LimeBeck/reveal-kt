@file:DependsOn("dev.limebeck:ko-te-jvm:0.2.4")

import dev.limebeck.revealkt.core.elements.*
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*
import dev.limebeck.templateEngine.KoTeRenderer
import dev.limebeck.templateEngine.runtime.standartLibrary.kote
import kotlinx.coroutines.runBlocking

title = "Hello from my presentation"

val renderer = KoTeRenderer {
    mapOf(
        kote
    )
}

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
    }
    slide {
        code {
            runBlocking {
                renderer.render("{{ kote() }}", mapOf()).getValueOrNull()!!
            }
        }
    }
}