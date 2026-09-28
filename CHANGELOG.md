# Unreleased

## Features

- `codeFromFile("../src/Service.kt", region = "fetch")` shows code from a real source file, selected by `// region` markers or a line range, with the language taken from the extension. `run` reloads the preview when a shown file changes, also outside the script directory.
- Slides take a `background { }` (image, color, gradient, video or iframe), their own `transition`, `transitionSpeed` and `backgroundTransition`, and `hidden = true`.
- Any element can be a fragment: `element.asFragment(effect, index)` or `fragment(index) { … }` for a group.
- `bundle --single-file` writes one self-contained HTML file with the runtime and referenced assets embedded, which opens without a server.
- `img` accepts URLs and `data:` URLs as well as asset paths.

## Build

- CI publishes the documentation site to `gh-pages` after every verified push to `master`, so the site no longer waits for a manual `scripts/publish-site.sh` run.

# 1.2.0

## Fixes

- `additionalCss {}` no longer mutates the shared default configuration, so CSS is not duplicated across live reloads or presentations.
- `code` snippets containing `</script>` or `<!--` no longer break the generated page.
- `BooleanAttributeDelegate` reads back the value it writes.
- The script compilation cache key includes the content of classpath entries, so an in-place CLI upgrade (for example a replaced `revealkt.jar`) cannot reuse scripts compiled against the old API.
- `run`, `pdf` and `bundle` use the same `--base-path` assets directory for serving/copying and for `loadAsset`. `bundle` gains `--base-path`; `pdf` gains the long `--base-path` name for `-b`.
- Internal server errors return HTTP 500, and error pages escape messages and stack traces.
- A change to an unrelated file can no longer swallow a pending script or asset change during live reload.
- `showHiddenSlides` reaches Reveal.js.
- `Code { ... }` trims by default, like `code(...)` and the primary constructor. Elements without extra styles no longer render an empty `style` attribute. QR codes get a default id and a well-formed data URI.

## Behavior changes

- `run` listens on `localhost` by default. Pass `--host 0.0.0.0` to allow access from other devices. The printed and opened URL uses `localhost` for wildcard hosts.
- `pdf` uses a free port by default (`--port 0`), so it does not collide with a running preview. `pdf -h` shows help; the host option is `--host` only.
- `QrCode`, `AssetLoader` and `s {}` moved to `dev.limebeck.revealkt.*` packages, so scripts can use `QrCode` without an import. The old `core.elements`, `dsl` and `utils` names remain as deprecated aliases.
- The CLI module targets Java 21, matching the version `doctor` requires. Libraries still target Java 11.

## Build

- npm packages no longer run install scripts during the build.
- CI pins `setup-jbang` to a commit and passes publishing secrets through environment variables.
- Installation docs, the JBang catalog and the example use 1.2.0 and the `revealkt.jar` release asset name.

# 1.1.1

## CLI distribution

- Publish a regular CLI JAR to Maven Central with runtime dependencies in its POM, resolved automatically by JBang. Keep the existing `dev.limebeck:revealkt-cli` coordinates and executable main class.
- Ship the self-contained `app-1.1.1-all.jar` through GitHub Releases. The main Maven JAR shrinks from approximately 297 MB to 4.84 MB while retaining presentation resources.
- Add a CI check that publishes to a temporary Maven repository, launches the CLI through JBang, and compiles and bundles a starter presentation. Local checks use unsigned publications; release publications remain signed.
- Update the installation guide, JBang catalog and example to 1.1.1.

# 1.1.0

## First-time use (stage 3)

- Add `doctor` with Java, script/resource/output directory and Chromium launch checks, corrective actions and nonzero failure status; it does not execute scripts or install software. Add explicit `chrome install --with-deps` for Ubuntu prerequisites.
- Keep the last working presentation and current slide when a live update fails. Show escaped diagnostics in a collapsible browser panel and the terminal, including for newly opened tabs; recover on the next successful save.
- Format compiler diagnostics with file, line, column and cause. Runtime diagnostics retain the available script stack line without inventing a column.
- Ship `init --example technical`, `lesson` and `custom-theme` with all assets, plus a simpler starter. Refuse to overwrite generated files.
- Add an installation-to-PDF guide, asset/theme/numbering reference and script trust documentation. Keep the roadmap in English.
- Avoid loading MathJax for ordinary slides without math markers. Math and Markdown presentations still use the CDN; full offline export remains outside this stage.
- Verify the guide and examples through a copied CLI JAR with an empty Java user home/Maven repository; test failure/recovery and doctor diagnostics.

## Stable CLI (P0)

- Updated Kotlin/Gradle, Ktor, Playwright and Reveal.js integration.
- Script paths accept bare filenames, relative/absolute paths and spaces.
- Repeated `bundle` updates HTML and resources. Removed source assets remain in the output; unrelated files are preserved. Use a fresh directory for a clean export.
- Live reload keeps complete event paths, watches newly created directories and reloads when assets change even if the HTML is identical.
- Custom CSS themes use stylesheet links; custom slide numbers reach Reveal.js as strings.
- Compilation and evaluation failures cause exports to exit unsuccessfully, with script diagnostics.
- PDF export waits for Reveal.js, print layout, fonts and images with bounded waits and browser diagnostics. Server and watcher resources are released on completion or failure.
- CI runs JVM, JavaScript and packaged CLI/browser/PDF checks before tag publication and uploads reports and CLI archives.

## Supported environment and checks

The supported CLI platform for this release is Linux x86-64 with Java 21 or 25 (CI: Ubuntu 24.04, Temurin). Other operating systems and older Java runtimes are not yet validated. The JVM library bytecode target remains 11; this is not a promise that the complete CLI runs on Java 11.

Build and run the complete checks:

```sh
./gradlew :reveal-kt:app:shadowJar
java -cp 'reveal-kt/app/build/libs/*' com.microsoft.playwright.CLI install --with-deps chromium
./gradlew build
```

The integration suite executes the packaged JAR in temporary directories, including paths with spaces. Chromium is required, and tests fail if it is missing. The PDF fixture contains two slides and local image/theme assets. MathJax is loaded from a CDN when slides contain math markers; the fixed fixtures use local resources. Other external presentation resources also need network access; failed resources abort PDF export. Readiness stages have a 30-second timeout each. Video playback and arbitrary asynchronous user JavaScript are outside the PDF readiness contract.

The required CI job is `Linux / Java …`; repository administrators must select these checks in branch protection if merge blocking is desired. Release publication is already gated by these jobs in the workflow.
