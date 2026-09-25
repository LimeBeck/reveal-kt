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
