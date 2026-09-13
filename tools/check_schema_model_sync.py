#!/usr/bin/env python3
"""Fail when the hub JSON schema and the Kotlin content model drift apart.

The schema declares `additionalProperties: false` on every block, so a field the
Kotlin model gains but the schema does not is invisible until someone authors
content that uses it — at which point CI fails on the content, not on the cause.
That is exactly how `ImageBlock.hero` broke the pipeline: the model had it, the
schema did not, and the first article to set `hero: true` failed validation.

This check compares the two directly, so the build breaks on the commit that
introduces the drift rather than on the content that trips over it.

    python3 tools/check_schema_model_sync.py
"""
from __future__ import annotations

import json
import os
import re
import sys

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
MODEL = os.path.join(
    ROOT, "shared/src/commonMain/kotlin/com/ynotlabs/cathopedia/content/model/HubContentModels.kt"
)
SCHEMA = os.path.join(ROOT, "content/schema/hub-content.schema.json")

BLOCK_RE = re.compile(
    r'@SerialName\("(?P<tag>\w+)"\)\s*\n\s*data class (?P<cls>\w+)\((?P<body>[^)]*)\)\s*:\s*Block',
    re.S,
)
FIELD_RE = re.compile(r"\bval (\w+)\s*:")
ENUM_RE = re.compile(r"enum class EntityType\s*\{([^}]*)\}")


def kotlin_blocks(source: str) -> dict[str, set[str]]:
    return {
        m.group("tag"): set(FIELD_RE.findall(m.group("body")))
        for m in BLOCK_RE.finditer(source)
    }


def kotlin_entity_types(source: str) -> set[str]:
    m = ENUM_RE.search(source)
    return set(re.findall(r"\b[A-Z_]+\b", m.group(1))) if m else set()


def schema_blocks(schema: dict) -> dict[str, set[str]]:
    out = {}
    for branch in schema["$defs"]["block"]["oneOf"]:
        tag = branch["properties"]["type"].get("const")
        if tag:
            out[tag] = set(branch["properties"]) - {"type"}
    return out


def main() -> int:
    source = open(MODEL, encoding="utf-8").read()
    schema = json.load(open(SCHEMA, encoding="utf-8"))

    kt = kotlin_blocks(source)
    sc = schema_blocks(schema)
    problems: list[str] = []

    # A regex that silently stops matching would turn this check into a no-op,
    # which is worse than not having it.
    if len(kt) < 5:
        problems.append(
            f"parsed only {len(kt)} block class(es) from HubContentModels.kt — "
            "the parser has probably rotted, not the model"
        )

    for tag in sorted(set(kt) | set(sc)):
        if tag not in sc:
            problems.append(f"block '{tag}' exists in Kotlin but not in the schema")
            continue
        if tag not in kt:
            problems.append(f"block '{tag}' exists in the schema but not in Kotlin")
            continue
        for field in sorted(kt[tag] - sc[tag]):
            problems.append(
                f"block '{tag}': Kotlin has '{field}', the schema does not — "
                "content using it will fail validation"
            )
        for field in sorted(sc[tag] - kt[tag]):
            problems.append(
                f"block '{tag}': the schema allows '{field}', Kotlin has no such field — "
                "content using it will be silently dropped at parse time"
            )

    kt_types = kotlin_entity_types(source)
    sc_types = set(schema["$defs"]["entityRef"]["properties"]["type"]["enum"])
    for t in sorted(kt_types - sc_types):
        problems.append(f"EntityType.{t} exists in Kotlin but not in the schema's entityRef enum")
    for t in sorted(sc_types - kt_types):
        problems.append(f"entityRef enum allows '{t}' but Kotlin's EntityType has no such value")

    if problems:
        for p in problems:
            print(f"ERROR: {p}", file=sys.stderr)
        print(f"\n{len(problems)} schema/model mismatch(es).", file=sys.stderr)
        return 1

    print(f"schema and model agree: {len(kt)} block types, {len(kt_types)} entity types")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
