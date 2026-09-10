# From a Kotlin script to HTML and PDF

This guide describes the CLI built from this revision. You need Linux x86-64 and Java 21 or 25. Chromium is needed for PDF export; you can write slides, preview them and bundle HTML before installing it. The CLI includes the Kotlin compiler, DSL, Reveal.js and example resources. Node.js, Gradle and Maven are not required to run the downloaded JAR.

## 1. Install the CLI

Run `java -version` and check that the selected runtime is Java 21 or 25. Set `JAVA_HOME` and `PATH` to your JDK if necessary.

Once 1.1.0 is published, download **`app-1.1.0.jar`** from [GitHub Releases](https://github.com/LimeBeck/reveal-kt/releases). Before publication, download the `cli-java-21` artifact from a successful [CI run](https://github.com/LimeBeck/reveal-kt/actions/workflows/main.yml) for the revision you want to use. GitHub may require you to sign in to download artifacts. Unzip it and take **`app-1.1.0.jar`**, the self-contained CLI. The smaller `app-jvm`, sources and javadoc JARs are not the CLI. Rename it to `revealkt.jar` and put it in a permanent directory, for example `$HOME/Applications/revealkt/`.

Until this revision is published, an older Maven Central artifact or JBang alias may not include `doctor` and the new examples. As an alternative to a CI artifact, build this revision once with `./gradlew :reveal-kt:app:shadowJar`, then copy `reveal-kt/app/build/libs/app-1.1.0.jar` out of the checkout. Subsequent commands only need that JAR.

In Bash or Zsh, define a command for this terminal (adjust the path):

```sh
revealkt() { java -jar "$HOME/Applications/revealkt/revealkt.jar" "$@"; }
revealkt --help
```

You can add the function to your shell configuration. Alternatively, replace `revealkt` in every command below with `java -jar /absolute/path/to/revealkt.jar`.

### Run with JBang

Alternatively, [install JBang](https://www.jbang.dev/download/) and use it to launch the CLI. After version 1.1.0 is published to Maven Central:

```sh
jbang run --java 21 dev.limebeck:revealkt-cli:1.1.0 --help
jbang run --java 21 dev.limebeck:revealkt-cli:1.1.0 init Demo
jbang run --java 21 dev.limebeck:revealkt-cli:1.1.0 run Demo/presentation/Demo.reveal.kts --host 127.0.0.1
```

To install the `revealkt` command for the rest of this guide, use [JBang app installation](https://www.jbang.dev/documentation/jbang/latest/app-installation.html):

```sh
jbang app setup
jbang app install --java 21 --name revealkt dev.limebeck:revealkt-cli:1.1.0
```

Follow any shell/PATH instructions from `jbang app setup`. If you already defined the `revealkt` shell function above, remove it with `unset -f revealkt` to use the installed command.

Before Maven publication, JBang can run the downloaded or locally built JAR directly:

```sh
jbang run --java 21 /absolute/path/to/app-1.1.0.jar --help
jbang app install --java 21 --name revealkt /absolute/path/to/app-1.1.0.jar
```

Keep that JAR at its installed path. The Maven coordinate requires publication to Maven Central; a GitHub release draft alone does not make it available. Chromium installation and all presentation commands below remain the same.

## 2. Generate a presentation

From the directory where you keep presentations:

```sh
revealkt init Demo --dirname "My presentation"
cd "My presentation"
revealkt doctor presentation/Demo.reveal.kts --output-dir out
```

The generated layout is:

```text
My presentation/
  build.gradle.kts                    # Optional IDE dependency configuration
  settings.gradle.kts
  presentation/
    Demo.reveal.kts                   # Edit this file
    assets/
      image.png
```

The Gradle files are for IDE integration and reference published libraries; you do not need to run Gradle to use the CLI. A development revision can precede the corresponding Maven publication.

`doctor` reports Java, the script and resource directories, output write access and a real Chromium launch. It **does not execute the script**. The output directory may be new: doctor checks the nearest existing parent and removes its temporary write probe. Missing Chromium produces `[FAIL]`, a nonzero exit code and an installation command; HTML preview and bundle remain usable.

To prepare PDF support:

```sh
revealkt chrome install
revealkt doctor presentation/Demo.reveal.kts --output-dir out
```

If Chromium cannot start because Ubuntu system libraries are missing, use `revealkt chrome install --with-deps`. This can request administrator privileges through the Playwright installer. Doctor never installs software automatically. After upgrading the CLI, run the installation command again: Playwright may require a different Chromium version.

## 3. Edit and preview

```sh
revealkt run presentation/Demo.reveal.kts --host 127.0.0.1 --port 8080
```

Open [localhost:8080](http://localhost:8080). Keep the terminal running. Use arrow keys or Space to navigate, Esc for the overview and S for speaker notes. Press Ctrl+C in the terminal to stop the server.

Open `presentation/Demo.reveal.kts` in a text editor. Change `My first presentation` to your title and save. The browser reloads after compilation. Change or add an image under `presentation/assets/` to try asset live reload. Newly created nested asset directories are watched as well.

A complete minimal script looks like this:

```kotlin
import dev.limebeck.revealkt.core.RevealKt
import dev.limebeck.revealkt.dsl.*
import dev.limebeck.revealkt.dsl.slides.*

title = "My first talk"
configuration {
    theme = RevealKt.Configuration.Theme.Predefined.BLACK
    slideNumber = RevealKt.Configuration.SlideNumber.Custom("c/t")
    pdfSeparateFragments = false
}
slides {
    regularSlide {
        +title { "Hello, Kotlin" }
        +regularText { "This text is rendered and escaped as HTML." }
    }
    regularSlide {
        +title { "Next steps" }
        +unorderedListOf("Write a script", "Preview it", "Share HTML or PDF")
    }
}
```

If an edit fails to compile or execute, the browser keeps the last working presentation and its current slide. An error panel shows the script path, available line/column and reason; you can collapse it and continue navigating. The terminal prints the same diagnostic. Correct the script and save: a successful update reloads the presentation and removes the panel. A newly opened tab also sees the last working HTML and current error. An error on the **initial** launch exits the command because there is no working presentation to retain.

## 4. Export HTML

In another terminal, define the same shell function and enter the presentation directory, then run:

```sh
revealkt bundle presentation/Demo.reveal.kts --output-dir "out html"
```

Open `out html/index.html` in a browser, or publish the entire `out html` directory to a static host. Keep `revealkt.js`, its accompanying resources and `assets/` together. Static exports do not contain a live reload connection or the development error panel.

Run the same command again to refresh HTML and assets. Removed source assets remain in an existing output directory, and unrelated files are preserved. Use a new directory when you need an export containing only current files.

## 5. Export PDF

```sh
revealkt pdf presentation/Demo.reveal.kts -o Demo.pdf
```

The command starts a temporary server, waits for Reveal.js, print layout, images and fonts, writes the PDF and exits. Stop a preview using the same port first, or choose another port:

```sh
revealkt pdf presentation/Demo.reveal.kts --port 8081 -o Demo.pdf
```

Compilation, execution, missing-resource and readiness failures return a nonzero exit code. A failed export does not write a new PDF; an older file at that location is retained. Check the command's exit status before treating an existing file as a new export. Create the parent directory of `-o` beforehand.

## 6. Explore the included examples

Run these from a directory outside the generated `Demo` project:

```sh
revealkt init RetryTalk --example technical
revealkt init NullSafety --example lesson
revealkt init BrandDeck --example custom-theme
```

- **RetryTalk:** a four-slide technical talk with Kotlin code, highlighting, takeaways and speaker notes.
- **NullSafety:** a three-slide lesson with incremental fragments and an exercise. Advance through the list items; PDF combines the fragments onto their slide.
- **BrandDeck:** a three-slide presentation with a self-contained CSS theme and an SVG pipeline diagram. Edit `presentation/assets/theme.css` while preview is running.

For example:

```sh
revealkt doctor BrandDeck/presentation/BrandDeck.reveal.kts
revealkt run BrandDeck/presentation/BrandDeck.reveal.kts --host 127.0.0.1
revealkt bundle BrandDeck/presentation/BrandDeck.reveal.kts --output-dir brand-html
revealkt pdf BrandDeck/presentation/BrandDeck.reveal.kts -o BrandDeck.pdf
```

Stop `run` before executing `pdf` on its port, or use a second port. `init` refuses to overwrite generated files already present at the destination; choose another `--dirname` to keep your work.

Read [assets, themes, numbering and export limits](presentation-reference.md) for details and troubleshooting.

## Script trust

A `.reveal.kts` file is executable Kotlin code, not a passive document. `run`, `bundle` and `pdf` execute it with your user's permissions, including access to files and the network. `run` executes it again on relevant changes, including asset changes. Review scripts from other people before running them, and keep side effects out of presentation scripts. There is no script sandbox. `doctor` only inspects paths and the environment; it does not compile or execute the script.

Preview listens on all interfaces by default; `--host 127.0.0.1` in this guide restricts it to your computer. The preview server has no authentication and is intended for development. Publish the static output when sharing the presentation.

## How this guide is checked

The packaged-CLI integration suite copies only the distribution JAR into a temporary directory and runs it with an empty Java user home and Maven repository. It generates the starter and all three examples, runs doctor, opens their HTML in Chromium and checks their PDFs. Chromium is an explicitly installed runtime prerequisite shared with the test runner. Examples do not request MathJax because they contain no formula markers. No source checkout, Gradle distribution or locally published RevealKt modules are used by those CLI processes. Compilation errors, live reload failure/recovery and missing Chromium are covered separately.
