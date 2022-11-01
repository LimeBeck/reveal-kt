import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.regularSlide
import dev.limebeck.revealkt.dsl.slides.slide
import dev.limebeck.revealkt.dsl.slides.verticalSlide
import core.elements.Title
import kotlin.test.Test


class CommonTest {
    @Test
    fun buildDsl() {
        val presentation = revealKt(title = "Hello from my presentation") {
//            config {
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
//            }
            slides {
                regularSlide {
                    autoanimate = true
                    title = "Test"
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
                        +title
//                        pic(href = "") {
//                            height = 400
//                        }
                        note {
                            "Some note"
                        }
                    }
                    markdownSlide {
                        //language=Markdown
                        """
                            # Title here
                            
                            * list 1
                            * list 2
                        """.trimIndent()
                    }
                }
            }
        }.build()
        presentation
    }
}