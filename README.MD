# RevealKt

[![CI](https://github.com/LimeBeck/reveal-kt/actions/workflows/main.yml/badge.svg)](https://github.com/LimeBeck/reveal-kt/actions/workflows/main.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/limebeck/reveal-kt)
![GitHub](https://img.shields.io/github/license/limebeck/reveal-kt)
[![Maven Central](https://img.shields.io/maven-central/v/dev.limebeck/revealkt-cli.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22dev.limebeck%22%20AND%20a:%22ko-te%22)


Kotlin DSL wrapper around Reveal JS presentation library

## Usage

Run server with presentation:
```bash
jbang revealkt@limebeck.dev run ./MyAwesomePresentation.reveal.kts
```

Bundle presentation to static html site:
```bash
jbang revealkt@limebeck.dev bundle ./MyAwesomePresentation.reveal.kts
```

Render presentation to pdf via playwright:
```bash
jbang revealkt@limebeck.dev pdf ./MyAwesomePresentation.reveal.kts -o myPresentation.pdf
```

> [!CAUTION]
> For now playwright requires to download chromium first (uses npm)

Playwright documentation: https://playwright.dev/java/docs/browsers

```bash
jbang revealkt@limebeck.dev chrome install
```

Uninstall chrome 
```bash
jbang revealkt@limebeck.dev chrome uninstall
```


Create new presentation from template
```bash
jbang revealkt@limebeck.dev init my-awesome-presentation
cd ./my-awesome-presentation
jbang revealkt@limebeck.dev run ./presentation/my-awesome-presentation.reveal.kts
```

## Add alias for `revealkt`

```bash
jbang app install revealkt@limebeck.dev
revealkt run ./presentation/my-awesome-presentation.reveal.kts
```

## Example

**MyAwesomeScript.reveal.kts**:
```kotlin
import qrcode.color.Colors

title = "My awesome presentation"

configuration {
    controls = false
    progress = false
}

slides {
    regularSlide {
        autoanimate = true
        +title { "Hello from my awesome presentation" }
    }
    regularSlide {
        autoanimate = true
        +qrCode("https://github.com/LimeBeck/reveal-kt") {
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
```
