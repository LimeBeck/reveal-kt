# RevealKt

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/limebeck/reveal-kt/CI)
![GitHub last commit](https://img.shields.io/github/last-commit/limebeck/reveal-kt)
![GitHub](https://img.shields.io/github/license/limebeck/reveal-kt)
[![Maven Central](https://img.shields.io/maven-central/v/dev.limebeck/revealkt-cli.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22dev.limebeck%22%20AND%20a:%22ko-te%22)


Kotlin DSL wrapper around Reveal JS presentation library

## Usage

Run server with presentation:
```bash
jbang revealkt@limebeck.dev ./MyAwesomePresentation.reveal.kts
```

## Example

**MyAwesomeScript.reveal.kts**:
```kotlin
import dev.limebeck.revealkt.core.elements.*
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

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