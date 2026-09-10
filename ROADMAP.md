# RevealKt Roadmap

Status: Stages 1–3 are complete for version 1.1.0. The implementation is verified locally on Linux / Java 21 and in CI on Java 21 and 25. Stage 4 is next. Updated September 10, 2026.

Maintain this roadmap in English. Follow the stages below in order and mark tasks complete only after their acceptance checks pass. Use idiomatic Kotlin for implementation.

The goal of the next development cycle is a reliable path from a Kotlin script to a browser presentation, static HTML and PDF. The primary audience is Kotlin developers, technical speakers and educators.

The estimates below describe development effort for one developer familiar with the project, not calendar commitments. Stages are sequential; minimal regression tests accompany fixes. Release numbers will be assigned after API compatibility has been checked.

## Sequence

| Stage | Priority | Estimate | Outcome |
| --- | --- | --- | --- |
| 1. Stabilize core workflows | P0 | 1–2 weeks | Predictable CLI and export behavior |
| 2. Automate quality checks | P0 | 1–2 weeks | Regressions detected before release |
| 3. Improve first-time use | P1 | 2–3 weeks | Users complete the workflow by following the documentation |
| 4. Strengthen the API and extensibility | P1/P2 | After stages 1–3; estimate depends on selected scope | Reusable components and stable extensions |

P0 is required for the next stable release; P1 is the next priority; P2 follows demonstrated demand. Checkboxes represent completed work, not prototypes or unfinished changes.

## 1. Stabilize core workflows

- [x] Complete the current dependency and Reveal.js integration upgrade; verify the build and packaged CLI.
- [x] Normalize script and resource paths: support bare filenames, relative and absolute paths, and spaces.
- [x] Allow repeated `bundle` into an existing directory: update resources and HTML; define stale-file behavior without deleting unrelated data.
- [x] Fix asset live reload: retain the full event path and watch newly created nested directories.
- [x] Fix custom CSS theme loading and forwarding of string slide-number formats.
- [x] Verify script compilation and execution failures: actionable diagnostics and nonzero exit codes for failed exports.
- [x] Manage the server lifecycle: stop the server, close the watcher and cancel coroutines after PDF export and on failure.
- [x] Replace the fixed PDF render delay with bounded waits for presentation and resource readiness, including timeout diagnostics.
- [x] Add regression coverage for the corrected defects.

**Done when:** a test presentation passes `init → run → script and asset edits → bundle → repeated bundle → pdf`; PDF exits independently, and an invalid script cannot produce a successful export result.

## 2. Automate quality checks

- [x] Run checks on pull requests and pushes to the main branch; separate verification from release publication.
- [x] Test DSL HTML rendering with meaningful assertions about structure, attributes and escaping.
- [x] Test configuration conversion from the DSL to values received by Reveal.js, including custom settings.
- [x] Add CLI integration tests for paths, repeated export and script failures.
- [x] Add browser checks for initialization, themes, numbering, assets and live reload.
- [x] Test PDF export using a small fixed presentation: document creation, expected page count and process termination.
- [x] Run core workflow checks through the packaged CLI JAR, including JS resources and template files.
- [x] Correct CI artifact and test-report upload paths.
- [x] Define supported Java versions and operating systems; configure checks starting with the primary platform.

**Done when:** required checks protect the core workflows, failures block publication, and test reports and the verified CLI are available as CI artifacts.

**Release milestone A — stable CLI:** stages 1 and 2 are complete, limitations are documented and changes are recorded in release notes. Included in version 1.1.0.

### P0 verification

- `./gradlew build --console=plain`: **BUILD SUCCESSFUL**, 10 tests without failures (6 application JVM tests, 2 DSL JVM tests and 2 DSL JS tests), including the final Kotlin refactoring.
- [Packaged CLI integration scenario](reveal-kt/app/src/jvmTest/kotlin/CliIntegrationTest.kt): `init`, bare filenames, relative/absolute paths and spaces, repeated bundle, preserved files, browser initialization and themes, numbering, script and nested-asset live reload, a two-page PDF, port release, compilation/evaluation failures and missing resources.
- [HTML and configuration](reveal-kt/app/src/jvmTest/kotlin/RenderingTest.kt), [watcher cancellation](reveal-kt/app/src/jvmTest/kotlin/WatcherTest.kt), [PDF timeout](reveal-kt/app/src/jvmTest/kotlin/PdfReadinessTest.kt).
- [CI](.github/workflows/main.yml): pull requests, pushes to `master`, Java 21/25 matrix, reports and CLI artifacts; tag publication depends on successful checks.
- [Changes, limitations and validation commands](CHANGELOG.md). MathJax uses a CDN; stale bundle files are retained. Repository administrators select required merge checks in branch protection; the workflow already gates release publication.

## 3. Improve first-time use

- [x] Write a step-by-step guide: installation → template → first slide → live reload → HTML → PDF.
- [x] Add `doctor` to diagnose Java, Chromium availability and required directories; report a concrete corrective action for each problem.
- [x] Make errors actionable: show the script path, line and cause when available from the compiler.
- [x] Preserve the last working presentation after a failed development update and clearly display the error.
- [x] Provide complete examples: a technical talk with code, a lesson with fragments, and a presentation with a custom theme and assets.
- [x] Document the `assets` layout, custom themes, numbering formats and HTML/PDF export limitations.
- [x] Explain the trust model: Kotlin scripts execute with the user's permissions.
- [x] Verify the instructions in a clean environment without local development artifacts.

**Done when:** a new user reaches HTML and PDF by following the documentation without modifying the project's source code or asking its author for help; environment checks explain missing dependencies.

**Release milestone B — self-service use:** stage 3 is complete; examples and instructions have been verified against the distributed CLI.

### Stage 3 verification

- `./gradlew build --console=plain`: **BUILD SUCCESSFUL**, 14 tests without failures (10 application JVM, 2 DSL JVM and 2 DSL JS tests), on Linux / Java 21.
- [Getting started](docs/getting-started.md) and [presentation reference](docs/presentation-reference.md) cover installation, editing, assets, exports, troubleshooting and script trust.
- [First-use integration tests](reveal-kt/app/src/jvmTest/kotlin/FirstUseIntegrationTest.kt) execute a copied CLI JAR with an empty Java user home and Maven repository, verify all four templates through HTML and PDF, block remote requests for local examples, and check actionable failures. Chromium and system libraries remain installed prerequisites.
- [Live-update regression tests](reveal-kt/app/src/jvmTest/kotlin/CliIntegrationTest.kt) verify compilation/runtime errors, retained slides, diagnostics in existing/new tabs and recovery after a successful save.

## 4. Strengthen the API and extensibility

### First: API stability

- [ ] Define boundaries between the DSL, script loader, HTML renderer and CLI; introduce modules only for a concrete need.
- [ ] Reduce duplicate configuration conversions and establish one Reveal.js adaptation layer.
- [ ] Define the supported public API and compatibility policy; verify existing examples when it changes.
- [ ] Define JVM and JS support, including resource loading; explicitly identify unsupported scenarios.
- [ ] Review the existing KSP/Gradle workarounds and simplify them after confirming correct builds.

### Then: capabilities driven by feedback

- [ ] Add reusable components: title slide, comparison, annotated code and speaker card.
- [ ] Establish theme and template extension mechanisms with examples.
- [ ] Design an explicit API for custom Reveal.js plugins and their resources.
- [ ] Define offline and reproducible-export requirements; document external dependencies and implement the selected scope.

**Ready for a separate release when:** the selected capability has a working example, behavioral tests and compatibility documentation. The whole stage does not have to ship in one release.

## Deferred

- Visual slide editor.
- Cloud storage and collaborative editing.
- Additional native platforms.
- A custom presentation engine.

Revisit these directions after concrete user scenarios and an implementation-cost assessment are available.

## Progress tracking

At the end of each stage:

1. Verify acceptance criteria and mark completed tasks with links to PRs or validation results.
2. Update known limitations and documentation.
3. Reassess the next stage using defects and user feedback.

The next cycle's key indicators are completion of the entire CLI workflow, documentation without manual workarounds, exports that terminate independently, and regressions detected in CI. Measure initial and repeated render times on a fixed presentation before setting performance targets.
