# Presentation reference

## Asset paths

The default resource root is the script's directory, regardless of the terminal's working directory. Put resources in its `assets` subdirectory:

```text
presentation/Talk.reveal.kts
presentation/assets/logo.svg
presentation/assets/images/chart.png
presentation/assets/theme.css
```

Use `img(src = "images/chart.png")`; the image DSL adds `assets/`. URLs (`https://…`, `data:…`) and absolute paths are used as they are. For `loadAsset("logo.svg")`, the JVM asset loader reads from the same script-adjacent directory. File and directory names can contain spaces; quote shell paths. Use forward slashes in HTML/CSS resource URLs.

Custom CSS links are already URLs: use `Theme.Custom("assets/theme.css")`, including the `assets/` prefix. URLs inside CSS resolve relative to the CSS file, so `url("images/chart.png")` in `assets/theme.css` points to `assets/images/chart.png`.

`run --base-path DIR`, `pdf --base-path DIR` and `bundle --base-path DIR` use `DIR/assets` instead of the `assets/` directory beside the script. The same directory is served or copied as `assets/` and read by `loadAsset`, so all three output modes see the same files.

## Code from source files

`codeFromFile` shows code from a real file, so a slide cannot drift from the code it describes. Relative paths resolve against the script's directory:

```kotlin
slides {
    regularSlide {
        +codeFromFile("../src/main/kotlin/Service.kt", region = "fetch", lineNumbers = "|1|2-3")
    }
}
```

```kotlin
class Service {
    // region fetch
    fun fetch(id: Int): String {
        val key = "item-$id"
        return cache[key] ?: load(key)
    }
    // endregion
}
```

- `region` shows the lines between `// region name` and `// endregion`. `#`, `--`, block and HTML comments also work, regions can nest, and marker lines are left out.
- `range = 10..20` shows 1-based source lines instead, or lines within the region when both are given. Nested region markers count as lines.
- The common indentation is removed. The highlight language comes from the file extension; pass `lang` to override it. `lineNumbers` is Reveal.js line highlighting, as in `code(lines = …)`.
- A missing file, region or range fails the script with a message that names it.
- `run` reloads the preview when a shown file changes, also outside the script and asset directories.

## Slide backgrounds and transitions

```kotlin
regularSlide {
    transition = RevealKt.Configuration.Transition.ZOOM
    transitionSpeed = RevealKt.Configuration.TransitionSpeed.FAST
    background {
        image = "hero.jpg"          // assets/hero.jpg; URLs also work
        size = "cover"
        opacity = 0.4
    }
    +title("Launch")
}
```

`background` also takes `color`, `gradient` (a CSS gradient), `position`, `repeat`, `video` with `videoLoop` and `videoMuted`, and `iframe` with `interactive`. `backgroundTransition` sets how the background changes. `hidden = true` skips the slide unless `showHiddenSlides` is enabled. Settings on a slide override the presentation `configuration`.

## Themes

A built-in theme:

```kotlin
configuration {
    theme = RevealKt.Configuration.Theme.Predefined.BLACK
}
```

A custom theme replaces the built-in theme rather than extending it. Core Reveal.js CSS still provides layout. Your CSS should style the viewport and presentation elements:

```kotlin
configuration {
    theme = RevealKt.Configuration.Theme.Custom("assets/theme.css")
}
```

```css
.reveal-viewport { background: #102c35; color: #e9f1ee; }
.reveal { font-family: system-ui, sans-serif; font-size: 32px; }
.reveal h1, .reveal h2 { color: #85ebbd; }
```

A `body` selector alone can lose to Reveal.js's `.reveal-viewport` rule. Setting `--r-background-color` alone also needs a rule that uses it. The bundled `custom-theme` example demonstrates a complete small theme with no external font dependency.

For small overrides while retaining a built-in theme, set `additionalCssStyle` in `configuration` instead.

## Slide numbers and fragments

```kotlin
configuration {
    slideNumber = RevealKt.Configuration.SlideNumber.Custom("c/t")
    pdfSeparateFragments = false
}
```

| Value | Meaning |
| --- | --- |
| `SlideNumber.Disable` | Hide the number (default). |
| `SlideNumber.Enable` | Show Reveal.js's default numbering. |
| `SlideNumber.Custom("h.v")` | Horizontal and vertical position separated by a dot. |
| `SlideNumber.Custom("h/v")` | Horizontal and vertical position separated by a slash. |
| `SlideNumber.Custom("c")` | Continuous slide number. |
| `SlideNumber.Custom("c/t")` | Continuous slide number and total. |

These types belong to `RevealKt.Configuration`.

Any element can appear as a step. `fragment { }` groups elements into one step; `asFragment()` wraps a single element. Steps with a lower `index` appear first, and steps with the same index appear together:

```kotlin
regularSlide {
    +title("Why")
    +regular("Fast feedback").asFragment(ListItem.Effect.FADE_UP)
    +fragment(index = 2) {
        +regular("Typed slides")
        +img("types.png")
    }
}
```

A fragment wraps its content in a `div`. Reveal.js stretches only direct children of a slide, so an `img` inside a fragment is not stretched.

Lists built with `unorderedListOf` or `orderedListOf` reveal subsequent items as fragments by default. Use `fragmented = false` for a static list. `pdfSeparateFragments = false` puts all fragment content on one slide page; when enabled, fragment steps can increase the number of PDF pages.

## Export limits

- HTML export is a directory by default. Copy all generated files to your static host. Keep the source script and private files outside the published directory.
- `bundle --single-file` writes one HTML file named after the script (`Talk.reveal.kts` → `Talk.html`) that opens without a server. The runtime and every `assets/` file the page references are embedded as data URLs, including `Theme.Custom` stylesheets and the relative `url()` references inside them. The file is several megabytes. Remote URLs, theme web fonts and MathJax still load from the network; without it, text falls back to system fonts. Assets referenced only from `additionalCssStyle` or loaded by your own scripts are not embedded, and missing assets are reported as warnings.
- Built-in JS/CSS is included in the CLI. The math plugin loads MathJax from a CDN only when slide content contains math markers (`$`, `\(`, `\[`, `\begin{`, a `.math` element or a `math/tex` script) or uses Markdown slides. Code blocks are ignored. The included examples contain no math markers. External images, fonts or custom URLs still require network access. Offline export is not currently guaranteed.
- PDF readiness uses bounded waits (30 seconds per readiness/navigation stage). Browser errors, failed requests and missing images abort export. Deliberately continuous network traffic can prevent readiness.
- Video playback and arbitrary asynchronous user code are outside the PDF readiness contract. Interactivity, speaker notes and live reload are not reproduced as interactive PDF features.
- A tall slide can occupy more than one PDF page. Keep content within the slide; the example PDFs are tested for expected page counts.
- Output assets removed from the source remain on repeat bundle. Use a fresh directory for a clean snapshot.

## Troubleshooting

| Symptom | Next action |
| --- | --- |
| `java` is missing or an unsupported class version is reported | Install/select JDK 21 or 25, then check `java -version`. |
| `doctor` reports missing Chromium | Run `revealkt chrome install` with this CLI version and the same user. |
| Chromium cannot launch on Ubuntu | Run `revealkt chrome install --with-deps`, then doctor again. |
| Output directory check fails | Choose a writable `--output-dir`; replace file/path conflicts or fix permissions. |
| Image is missing | Check `assets/` beside the script and the case-sensitive relative `img(src=...)` path. |
| Custom theme has no visible effect | Check the `assets/` URL and selector specificity; start from `init --example custom-theme`. |
| A save shows an error panel | Read `file:line:column: error: reason`, fix the script and save. The working deck remains visible. |
| Initial preview exits with an error | Correct the diagnostic and start `run` again. No successful deck was available yet. |
| PDF reports address already in use | Stop preview or choose another `--port`. |
| PDF times out or reports a resource URL | Check that URL/network access and the browser diagnostic. Test a bundled example to distinguish environment and presentation issues. |

Doctor does not validate arbitrary script behavior or remote URLs. Compilation diagnostics include line and column when supplied by Kotlin. Runtime diagnostics use the line from the script stack frame when available, without inventing a column; without such a frame they retain the file and cause without inventing a location.
