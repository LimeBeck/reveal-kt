# RevealKt

[![CI](https://github.com/LimeBeck/reveal-kt/actions/workflows/main.yml/badge.svg)](https://github.com/LimeBeck/reveal-kt/actions/workflows/main.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/limebeck/reveal-kt)
![GitHub](https://img.shields.io/github/license/limebeck/reveal-kt)
[![Maven Central](https://img.shields.io/maven-central/v/dev.limebeck/revealkt-cli.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/dev.limebeck/revealkt-cli)


Kotlin DSL wrapper around Reveal JS presentation library

## Get started

Use the [step-by-step guide](docs/getting-started.md) to install the CLI as a standalone JAR or through JBang, generate a presentation, preview edits and export HTML/PDF. It also explains script trust and how to diagnose the environment with `doctor`.

```sh
# After defining revealkt as described in the guide:
revealkt init Demo
revealkt doctor Demo/presentation/Demo.reveal.kts
revealkt run Demo/presentation/Demo.reveal.kts --host 127.0.0.1
```

The CLI includes three complete examples: `init --example technical`, `lesson` and `custom-theme` (provide a presentation name as well). They cover code, incremental fragments, custom CSS and image assets.

- [Assets, themes, numbering and troubleshooting](docs/presentation-reference.md)
- [Roadmap](ROADMAP.md)
- [Changes, supported runtimes and validation](CHANGELOG.md)

The supported CLI platform is Linux x86-64 with Java 21 or 25. Chromium is required for PDF export. A Kotlin script executes with your user's permissions; review scripts before running them.
