import qrcode.color.Colors

title = "Hello from my presentation"

configuration {
    controls = false
    progress = false
}

slides {
    regularSlide {
        autoanimate = true
        +title { "Test 3" }
    }
    regularSlide {
        autoanimate = true
        +qrCode("https://github.com/Kotlin/KEEP/blob/master/proposals/scripting-support.md#implementation-status") {
            stretch = true
            transformBuilder {
                val logo = loadAsset("logo2.png")
                it.withSize(20).withColor(Colors.css("#B125EA")).withLogo(logo, 150, 150, clearLogoArea = true)
            }
        }
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