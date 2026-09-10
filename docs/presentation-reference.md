# Presentation reference

## Asset paths

The default resource root is the script's directory, regardless of the terminal's working directory. Put resources in its `assets` subdirectory:

```text
presentation/Talk.reveal.kts
presentation/assets/logo.svg
presentation/assets/images/chart.png
presentation/assets/theme.css
```

Use `img(src = "images/chart.png")`; the image DSL adds `assets/`. For `loadAsset("logo.svg")`, the JVM asset loader reads from the same script-adjacent directory. File and directory names can contain spaces; quote shell paths. Use forward slashes in HTML/CSS resource URLs.

Custom CSS links are already URLs: use `Theme.Custom("assets/theme.css")`, including the `assets/` prefix. URLs inside CSS resolve relative to the CSS file, so `url("images/chart.png")` in `assets/theme.css` points to `assets/images/chart.png`.

`run --base-path DIR` and `pdf -b DIR` change the directory whose `assets/` the server exposes. They do not change the script file or `loadAsset` resolution. `bundle` always copies assets beside the script. Keep the default layout when the same presentation needs all three output modes.

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

These types belong to `RevealKt.Configuration`. Lists built with `unorderedListOf` or `orderedListOf` reveal subsequent items as fragments by default. Use `fragmented = false` for a static list. `pdfSeparateFragments = false` puts all fragment content on one slide page; when enabled, fragment steps can increase the number of PDF pages.

## Export limits

- HTML export is a directory, not a standalone HTML file. Copy all generated files to your static host. Keep the source script and private files outside the published directory.
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
