#!/usr/bin/env python3
"""
Cathopedia content validator.

Runs in CI before any content is seeded into the app. Checks:
  1. Every hub file validates against hub-content.schema.json.
  2. Every string file validates against hub-strings.schema.json.
  3. Every *Key referenced by a hub exists in the default-language string file.
  4. Non-default languages report missing keys (warning unless --strict).
  5. No orphan strings (defined but never referenced).
  6. No dangling internal ids (articleIds / diagramIds / factSheetId / etc.).
  7. Inline [[type:id|label]] links are well-formed and use known entity types.

Usage:
    python tools/validate_content.py --root content --schemas schema [--strict]
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

try:
    from jsonschema import Draft202012Validator
except ImportError:  # keep the script usable without the dependency
    Draft202012Validator = None

DEFAULT_LANG = "en"
KEY_SUFFIXES = ("Key", "Keys")
ENTITY_TYPES = {
    "pope", "saint", "church", "article", "place", "document", "artwork", "council", "prayer",
}
LINK_RE = re.compile(r"\[\[([a-z_]+):([a-z0-9_.]+)(\|[^\]]+)?\]\]")


def collect_keys(node, out: set[str]) -> None:
    """Walk a hub document and collect every value of a *Key / *Keys field."""
    if isinstance(node, dict):
        for name, value in node.items():
            if name.endswith("Keys") and isinstance(value, list):
                out.update(v for v in value if isinstance(v, str))
            elif name.endswith("Key") and isinstance(value, str):
                out.add(value)
            else:
                collect_keys(value, out)
    elif isinstance(node, list):
        for item in node:
            collect_keys(item, out)


def collect_ids(hub: dict) -> dict[str, set[str]]:
    return {
        "article": {a["id"] for a in hub.get("articles", [])},
        "diagram": {d["id"] for d in hub.get("diagrams", [])},
        "factSheet": {f["id"] for f in hub.get("factSheets", [])},
        "stepper": {s["id"] for s in hub.get("steppers", [])},
        "timeline": {t["id"] for t in hub.get("timelines", [])},
        "section": {s["id"] for s in hub.get("sections", [])},
    }


def check_references(hub: dict, ids: dict[str, set[str]], errors: list[str]) -> None:
    for section in hub.get("sections", []):
        sid = section["id"]
        for aid in section.get("articleIds", []):
            if aid not in ids["article"]:
                errors.append(f"section {sid}: unknown articleId {aid}")
        for did in section.get("diagramIds", []):
            if did not in ids["diagram"]:
                errors.append(f"section {sid}: unknown diagramId {did}")
        for field, bucket in (("factSheetId", "factSheet"), ("stepperId", "stepper"), ("timelineId", "timeline")):
            ref = section.get(field)
            if ref and ref not in ids[bucket]:
                errors.append(f"section {sid}: unknown {field} {ref}")
        if section["status"] == "PUBLISHED":
            layout = section["layout"]
            required = {
                "ARTICLES": bool(section.get("articleIds")),
                "DIAGRAM": bool(section.get("diagramIds")),
                "STEPPER": bool(section.get("stepperId")),
                "TIMELINE": bool(section.get("timelineId")),
                "FACT_SHEET": bool(section.get("factSheetId")),
                "COLLECTION": bool(section.get("collectionQuery")),
            }.get(layout, True)
            if not required:
                errors.append(f"section {sid}: PUBLISHED with layout {layout} but no matching content attached")

    for article in hub.get("articles", []):
        if article["sectionId"] not in ids["section"]:
            errors.append(f"article {article['id']}: unknown sectionId {article['sectionId']}")
        for block in article["blocks"]:
            if block["type"] == "diagramRef" and block["diagramId"] not in ids["diagram"]:
                errors.append(f"article {article['id']}: unknown diagramId {block['diagramId']}")
            if block["type"] == "stepperRef" and block["stepperId"] not in ids["stepper"]:
                errors.append(f"article {article['id']}: unknown stepperId {block['stepperId']}")
            if block["type"] == "timelineRef" and block["timelineId"] not in ids["timeline"]:
                errors.append(f"article {article['id']}: unknown timelineId {block['timelineId']}")


def check_inline_links(strings: dict[str, str], lang: str, errors: list[str]) -> None:
    for key, text in strings.items():
        for entity_type, _entity_id, _label in LINK_RE.findall(text):
            if entity_type not in ENTITY_TYPES:
                errors.append(f"[{lang}] {key}: unknown entity link type '{entity_type}'")
        for fragment in re.findall(r"\[\[[^\]]*\]?\]?", text):
            if not LINK_RE.fullmatch(fragment):
                errors.append(f"[{lang}] {key}: malformed entity link {fragment!r}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default="content")
    parser.add_argument("--schemas", default="schema")
    parser.add_argument("--strict", action="store_true", help="treat missing translations as errors")
    args = parser.parse_args()

    root = Path(args.root)
    schema_dir = Path(args.schemas)
    errors: list[str] = []
    warnings: list[str] = []

    hub_validator = strings_validator = None
    if Draft202012Validator is not None:
        hub_validator = Draft202012Validator(json.loads((schema_dir / "hub-content.schema.json").read_text()))
        strings_validator = Draft202012Validator(json.loads((schema_dir / "hub-strings.schema.json").read_text()))
    else:
        warnings.append("jsonschema not installed — skipping schema validation")

    for hub_path in sorted((root / "hubs").glob("*.json")):
        hub = json.loads(hub_path.read_text())
        if hub_validator:
            for err in sorted(hub_validator.iter_errors(hub), key=lambda e: list(e.path)):
                errors.append(f"{hub_path.name}: {'/'.join(str(p) for p in err.path)}: {err.message}")

        ids = collect_ids(hub)
        check_references(hub, ids, errors)

        referenced: set[str] = set()
        collect_keys(hub, referenced)

        hub_id = hub["hub"]["id"]
        string_files = sorted((root / "strings").glob(f"{hub_id}.*.json"))
        if not string_files:
            errors.append(f"{hub_path.name}: no string files found for hub {hub_id}")
            continue

        for sf in string_files:
            doc = json.loads(sf.read_text())
            if strings_validator:
                for err in strings_validator.iter_errors(doc):
                    errors.append(f"{sf.name}: {err.message}")

            lang = doc["lang"]
            defined = set(doc["strings"])
            missing = sorted(referenced - defined)
            orphans = sorted(defined - referenced)

            if missing:
                bucket = errors if (lang == DEFAULT_LANG or args.strict) else warnings
                bucket.append(f"{sf.name}: {len(missing)} missing key(s): {', '.join(missing[:8])}"
                              + (" …" if len(missing) > 8 else ""))
            if orphans:
                warnings.append(f"{sf.name}: {len(orphans)} unused key(s): {', '.join(orphans[:8])}"
                                + (" …" if len(orphans) > 8 else ""))

            check_inline_links(doc["strings"], lang, errors)

        print(f"✓ {hub_path.name}: {len(ids['section'])} sections, {len(ids['article'])} articles, "
              f"{len(ids['diagram'])} diagrams, {len(referenced)} string keys, "
              f"{len(string_files)} language(s)")

    for w in warnings:
        print(f"warning: {w}", file=sys.stderr)
    for e in errors:
        print(f"ERROR: {e}", file=sys.stderr)

    return 1 if errors else 0


if __name__ == "__main__":
    raise SystemExit(main())
