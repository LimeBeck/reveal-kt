#!/usr/bin/env bash
set -euo pipefail
repo_root=$(cd "$(dirname "$0")/.." && pwd)
smoke_dir=$(mktemp -d)
trap 'rm -rf "$smoke_dir"' EXIT
version="0.0.0-jbang-test-$(date +%s)"
cd "$repo_root"
./gradlew \
  :reveal-kt:lib-dsl:publishJvmPublicationToMavenLocal \
  :reveal-kt:script-definition:publishMavenPublicationToMavenLocal \
  :reveal-kt:script-loader:publishMavenPublicationToMavenLocal \
  :reveal-kt:app:publishJvmPublicationToMavenLocal \
  -x dokkaGeneratePublicationHtml \
  -PunsignedPublication -PrevealKtVersion="$version" -Dmaven.repo.local="$smoke_dir/repository" --console=plain
cd "$smoke_dir"
cli() {
  jbang run --repos "smoke=file://$smoke_dir/repository" "dev.limebeck:revealkt-cli:$version" "$@"
}
cli --help
cli init Demo
cli bundle Demo/presentation/Demo.reveal.kts --output-dir output
test -s output/index.html
test -s output/revealkt.js
echo 'JBang resolved the thin CLI and compiled and bundled a presentation.'
