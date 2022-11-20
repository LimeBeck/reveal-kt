import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.core.elements.ListItem
import dev.limebeck.revealkt.core.elements.RegularText
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.regularSlide
import dev.limebeck.revealkt.dsl.slides.slide
import dev.limebeck.revealkt.dsl.slides.verticalSlide
import dev.limebeck.revealkt.core.elements.Title
import dev.limebeck.revealkt.core.elements.UnorderedList
import kotlin.test.Test


class CommonTest {
    @Test
    fun buildDsl() {
        val presentation = revealKt(title = "Hello from my presentation") {

            title = "(Де)мистифицируем Gradle"

            configuration {
                controls = false
                progress = true
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
                    +title(fitText = true) { "(Де)мистифицируем" }
                    +img(src = "gradle-white-primary.png")
                    +note("Спросить, кто вообще пользовался гредлом")
                }
                regularSlide {
                    +smallTitle { "Кто я такой?" }
                    +note("Рассказать, что я вкатился сначала на гредл, а потом только попробовал мавен")
                    +row {
                        column {
                            +img(src = "me.jpg") {
                                height = 400
                            }
                        }
                        column {
                            +UnorderedList(
                                elements = listOf(
                                    "Фанат Linux с 15 лет",
                                    "Ex-фанат Python",
                                    "Вкатился с Python на Kotlin",
                                    "Фанат Kotlin 3 года \uD83E\uDD37\u200D♂️",
                                    "Начал пользоваться Gradle раньше Maven",
                                    "Писал микросервисы",
                                ).mapIndexed { index, string ->
                                    ListItem(
                                        fragmented = index != 0,
                                        effect = ListItem.Effect.FADE_UP,
                                        element = RegularText { string }
                                    )
                                }
                            )
                        }
                    }
                }
                regularSlide {
                    +title(fitText = true) { "DANGER" }
                    +smallTitle { "Холиварная тема" }
                }
            }
        }.build()
        presentation
    }
}