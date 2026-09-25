#!/usr/bin/env python3
"""Build the documentation and real RevealKt example bundles into one static site."""
import argparse
import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / 'docs'
TEMPLATES = ROOT / 'reveal-kt/app/src/jvmMain/resources/examples'
PREFIX = 'window.revealKtExamples = '


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--cli', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args()
    cli = args.cli.resolve(strict=True)
    output = args.output.resolve()
    # Restrict replacement to generated build output, never source directories.
    if not output.is_relative_to(ROOT / 'build') or output == ROOT / 'build':
        parser.error('--output must be a subdirectory of the repository build directory')
    java = str(Path(os.environ['JAVA_HOME']) / 'bin/java') if os.environ.get('JAVA_HOME') else 'java'
    data = (DOCS / 'assets/examples.js').read_text(encoding='utf-8')
    examples = json.loads(data.removeprefix(PREFIX).rstrip().removesuffix(';'))
    output.parent.mkdir(parents=True, exist_ok=True)
    # Render everything before replacing the last successful website.
    with tempfile.TemporaryDirectory(prefix='revealkt-site-', dir=output.parent) as work:
        work = Path(work)
        site = work / 'site'
        (site / 'assets').mkdir(parents=True)
        (work / 'java-home').mkdir()
        for name in ('index.html', '.nojekyll'):
            shutil.copy2(DOCS / name, site / name)
        for name in ('site.css', 'site.js', 'favicon.svg'):
            shutil.copy2(DOCS / 'assets' / name, site / 'assets' / name)
        for example in examples:
            script = TEMPLATES / example['id'] / 'presentation.reveal.kts'
            example['source'] = script.read_text(encoding='utf-8')
            target = site / 'examples' / example['id']
            print(f"Rendering {example['id']} with RevealKt…", flush=True)
            subprocess.run([
                java, f'-Duser.home={work / "java-home"}', '-jar', str(cli),
                'bundle', str(script), '--output-dir', str(target),
            ], check=True, cwd=ROOT)
            if not (target / 'index.html').is_file() or not (target / 'revealkt.js').is_file():
                raise RuntimeError(f'Incomplete example bundle: {target}')
        (site / 'assets/examples.js').write_text(
            PREFIX + json.dumps(examples, ensure_ascii=False, indent=2) + ';\n', encoding='utf-8',
        )
        if output.exists():
            shutil.rmtree(output)
        shutil.move(str(site), output)
    print(f'Website built: {output}')


if __name__ == '__main__':
    main()
