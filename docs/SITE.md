# Documentation website

The English-language site is a standalone static page in `docs/index.html`.
The dark theme follows the repository banner. The Gradle `buildSite` task builds
the packaged CLI, compiles all three bundled Kotlin examples, and assembles a
static website in `build/site`. Embedded presentations use the actual generated
Reveal.js runtime. The Markdown guides remain available separately.

## Preview

From the repository root:

```sh
./gradlew buildSite
python3 -m http.server 4173 --bind 127.0.0.1 --directory build/site
```

Open <http://127.0.0.1:4173>. Serve the generated site, not `docs/`, which contains
only the documentation sources. Building requires Linux x86-64, JDK 21 or 25,
Python 3, and the normal Gradle build dependencies; Chromium is not needed for
HTML bundles.
Clipboard availability depends on browser permissions; a manual-copy message
appears when copying is unavailable.

## Publish

The public site is hosted at <https://limebeck.github.io/reveal-kt/>.
GitHub Pages uses **Deploy from a branch → gh-pages → / (root)**.
The `.nojekyll` file keeps the site static.

To publish this checkout's current website (including uncommitted site edits):

```sh
bash scripts/publish-site.sh
```

The script needs the build prerequisites above, Node.js, Git, a configured Git
author, and push access to `origin`. It runs `./gradlew buildSite`, checks
JavaScript syntax, then copies the generated website into a temporary checkout
of `gh-pages`. It commits and
pushes normally, without force-pushing or changing your current branch. The
source files stay in `docs/`; GitHub Pages builds the published branch after the
push. Changes to `master` alone do not publish the site: run the command above.

For a new fork, enable Pages once in **Settings → Pages** after the first push,
selecting `gh-pages` and `/ (root)`. Relative asset URLs also support repository
subpaths and other static hosts.

## Maintain

- `index.html`: documentation, navigation and page structure.
- `assets/site.css`: responsive layout and visual styles.
- `assets/site.js`: source tabs, copy buttons and mobile menu.
- `assets/examples.js`: example descriptions and source copies for local editing.
- `../scripts/build-site.py`: assembles the site and invokes the CLI `bundle` command.
- Root Gradle `buildSite`: depends on `:reveal-kt:app:shadowJar`, tracks inputs and
  outputs, and writes `build/site/examples/{technical,lesson,custom-theme}/`.

After editing the packaged CLI templates:

```sh
python3 scripts/sync-site-examples.py
python3 scripts/sync-site-examples.py --check
```

The sync script refreshes the source copies for editing. During `buildSite`,
source snippets are always read directly from the same Kotlin templates that
are compiled, so displayed code and rendered decks stay in sync. Review the
example descriptions when templates change. Generated bundles are kept under
the ignored `build/` directory and never checked into the source branch. A
failed rendering stops publication and keeps the last successful site output.

Each example is embedded in an iframe and can also be opened as a standalone
presentation. Reveal.js handles slide navigation, fragments, and overview. The
page includes the complete source and local `init` / `run` commands.

For a smoke check, visit at desktop and mobile widths, switch all three examples,
check real slide navigation and fragments, source and launch commands, keyboard
tab selection, and copy buttons,
open the FAQ, and use the mobile navigation. The documentation stays readable
without JavaScript; the example source tabs require JavaScript; CLI commands and a source link are provided as a fallback.
