#!/usr/bin/env python3
"""Sync website Kotlin snippets with packaged CLI examples.

Run with --check in reviews to detect stale copies without changing files.
Example descriptions in examples.js are maintained separately.
"""
import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEMPLATES = ROOT / 'reveal-kt/app/src/jvmMain/resources/examples'
DATA = ROOT / 'docs/assets/examples.js'
PREFIX = 'window.revealKtExamples = '


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    current = DATA.read_text(encoding='utf-8')
    examples = json.loads(current.removeprefix(PREFIX).rstrip().removesuffix(';'))
    for example in examples:
        example['source'] = (TEMPLATES / example['id'] / 'presentation.reveal.kts').read_text(encoding='utf-8')
    updated = PREFIX + json.dumps(examples, ensure_ascii=False, indent=2) + ';\n'
    if args.check:
        if current != updated:
            parser.exit(1, 'Website examples are stale. Run python3 scripts/sync-site-examples.py\n')
        print('Website snippets match the CLI templates.')
    else:
        DATA.write_text(updated, encoding='utf-8')
        print('Updated website snippets. Review example descriptions if templates changed.')


if __name__ == '__main__':
    main()
