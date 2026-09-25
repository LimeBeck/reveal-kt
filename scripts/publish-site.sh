#!/usr/bin/env bash
# Publish the static documentation without changing the current checkout.
set -euo pipefail

repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
remote_url="$(git -C "$repo_root" remote get-url origin)"
# Always render from current CLI sources before publishing.
(cd "$repo_root" && ./gradlew buildSite --console=plain)
site_dir="$repo_root/build/site"
node --check "$site_dir/assets/site.js"
node --check "$site_dir/assets/examples.js"

publish_dir="$(mktemp -d "${TMPDIR:-/tmp}/revealkt-pages.XXXXXX")"
trap 'rm -rf -- "$publish_dir"' EXIT

git -C "$publish_dir" init --quiet
# Use the repository's configured author for the generated-site commit.
git -C "$publish_dir" config user.name "$(git -C "$repo_root" var GIT_AUTHOR_IDENT | sed 's/ <.*//')"
git -C "$publish_dir" config user.email "$(git -C "$repo_root" var GIT_AUTHOR_IDENT | sed 's/.*<\([^>]*\)>.*/\1/')"
git -C "$publish_dir" remote add origin "$remote_url"
branch_ref="$(git -C "$publish_dir" ls-remote --heads origin refs/heads/gh-pages)"
if [[ -n "$branch_ref" ]]; then
  git -C "$publish_dir" fetch --quiet --depth=1 origin gh-pages
  git -C "$publish_dir" checkout --quiet -b gh-pages FETCH_HEAD
else
  git -C "$publish_dir" checkout --quiet --orphan gh-pages
fi

# Replace only the generated website directories; preserve unrelated branch files.
rm -rf -- "$publish_dir/assets" "$publish_dir/examples"
cp -R "$site_dir/." "$publish_dir/"
git -C "$publish_dir" add --all -- index.html .nojekyll assets examples
if git -C "$publish_dir" diff --cached --quiet; then
  echo 'The published branch already contains this version.'
  exit 0
fi
git -C "$publish_dir" commit --quiet -m 'Publish RevealKt documentation website'
# A concurrent update fails safely; never rewrite the deployment history.
git -C "$publish_dir" push origin HEAD:refs/heads/gh-pages
echo 'Published gh-pages. GitHub Pages will build the site from the branch root.'
