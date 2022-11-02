import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.regularSlide
import dev.limebeck.revealkt.dsl.slides.slide
import dev.limebeck.revealkt.dsl.slides.verticalSlide
import dev.limebeck.revealkt.core.elements.Title
import kotlin.test.Test


class CommonTest {
    @Test
    fun buildDsl() {
        val presentation = revealKt(title = "Hello from my presentation") {
            title = "Hello from my presentation"

            configuration {
                controls = false
                progress = false

//                styles {
//                    cssFile(href = "")
//                    rawCSS {
//                        """
//
//                        """.trimIndent()
//                    }
//                    css {
//
//                    }
//                }
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
        }.build()
        presentation
    }
}