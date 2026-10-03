#!/usr/bin/env python3
"""
Machine-translate Cathopedia's English content into the app's other languages
with Claude.

What it translates (English is always the source):
  strings   content/strings/<hub>.en.json      -> <hub>.<lang>.json
  entities  content/<type>/<id>.json text.en    -> text.<lang>
            (saints, popes, apostles, churches, apparitions, miracles,
             feasts, documents, mysteries)
  ui        shared/.../i18n/Strings.kt         -> StringsXX.kt   (generated)
            VESTMENT_TRANSLATIONS_IT keys      -> VestmentStringsXX.kt

What it never touches:
  - prayers: prayer texts must come from published, citable sources (see
    content/README.md), never from a machine. Latin comes from the same
    sourcing work.
  - anything a human wrote: a target value that exists but was not written by
    this tool (no entry in state/<lang>.json) is left alone forever.

Incremental: state/<lang>.json records the hash of the English text each
machine translation was made from. Re-running only translates new keys and
keys whose English changed since.

Usage:
  pip install anthropic
  export ANTHROPIC_API_KEY=...
  python3 tools/translate/translate.py --lang it --dry-run     # what would run, est. cost
  python3 tools/translate/translate.py --lang it               # Batches API (50% price)
  python3 tools/translate/translate.py --lang de es pt pl nl ta
  python3 tools/translate/translate.py --lang it --only ui --sync   # small run, no batch wait
  python3 tools/translate/translate.py --resume                # collect a batch after a crash

After a run: ./gradlew :shared:compileContent  (CONTENT_VERSION is bumped for you).
"""
from __future__ import annotations

import argparse
import hashlib
import json
import re
import sys
import time
from dataclasses import dataclass, field
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"
I18N = ROOT / "shared/src/commonMain/kotlin/com/ynotlabs/cathopedia/i18n"
CONTENT_LOADER = ROOT / "shared/src/commonMain/kotlin/com/ynotlabs/cathopedia/data/ContentLoader.kt"
HERE = Path(__file__).resolve().parent
STATE_DIR = HERE / "state"
GLOSSARY_DIR = HERE / "glossary"
PENDING_FILE = STATE_DIR / "pending_batch.json"

MODEL = "claude-opus-5-5"
# Batches price for this model (USD per million tokens), for the dry-run estimate only.
BATCH_PRICE_IN, BATCH_PRICE_OUT = 2.0, 10.0

LANGUAGES = {
    "fr": "French",
    "it": "Italian",
    "es": "Spanish",
    "pt": "Portuguese",
    "de": "German",
    "nl": "Dutch",
    "pl": "Polish",
    "ta": "Tamil",
}
# Hand-written chrome tables. The tool fills only their gaps in a report; it
# never regenerates them.
HANDWRITTEN_UI = {"fr", "it"}

ENTITY_TYPES = ["saints", "popes", "apostles", "churches", "apparitions",
                "miracles", "feasts", "documents", "mysteries"]

ENTITY_HINTS = {
    "saints": "a saint's biography",
    "popes": "a pope's biography",
    "apostles": "an apostle's biography",
    "churches": "a church or basilica",
    "apparitions": "a Marian apparition",
    "miracles": "a Eucharistic or other approved miracle",
    "feasts": "a liturgical feast",
    "documents": "a papal or conciliar document",
    "mysteries": "a mystery of the Rosary",
}

# Strings files whose text is sourced prayer (Stations meditations, versicles):
# like prayers, these come from published texts, never from a machine.
SOURCED_STRING_FILES = {"stations"}

GALLERY_KT = ROOT / "shared/src/commonMain/kotlin/com/ynotlabs/cathopedia/ui/MiracleGalleries.kt"

STRING_FILE_HINTS = {
    "screens": "text on a dedicated app screen",
    "galleries": "caption of a photograph documenting a Eucharistic miracle (relic, reliquary, church, painting or document)",
}

# Requests are grouped per source file and capped so one bad answer costs little.
MAX_ITEMS_PER_REQUEST = 60
MAX_WORDS_PER_REQUEST = 2500

PLACEHOLDER = re.compile(r"\{[A-Za-z_][A-Za-z0-9_]*\}")


# ---------------------------------------------------------------- segments

@dataclass
class Segment:
    id: str          # stable, unique within a language
    source: str      # English text
    group: str       # requests never mix groups (one file / one table)
    hint: str        # what the text is, for the translator


@dataclass
class LangJob:
    lang: str
    segments: list[Segment] = field(default_factory=list)


def sha(text: str) -> str:
    return hashlib.sha1(text.encode("utf-8")).hexdigest()[:16]


def load_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def write_json(path: Path, data) -> None:
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def load_state(lang: str) -> dict[str, str]:
    p = STATE_DIR / f"{lang}.json"
    return load_json(p) if p.exists() else {}


def save_state(lang: str, state: dict[str, str]) -> None:
    STATE_DIR.mkdir(parents=True, exist_ok=True)
    write_json(STATE_DIR / f"{lang}.json", dict(sorted(state.items())))


def needs_translation(seg_id: str, source: str, existing: str | None, state: dict[str, str]) -> bool:
    """Missing -> yes. Machine-written and English since changed -> yes. Human-written -> never."""
    if existing is None or existing == "":
        return True
    recorded = state.get(seg_id)
    return recorded is not None and recorded != sha(source)


# Hub strings -------------------------------------------------------------

def hub_string_segments(lang: str, state: dict[str, str]) -> list[Segment]:
    out = []
    for en_file in sorted((CONTENT / "strings").glob("*.en.json")):
        hub = en_file.name.removesuffix(".en.json")
        if hub in SOURCED_STRING_FILES:
            continue
        en = load_json(en_file)["strings"]
        target_file = CONTENT / "strings" / f"{hub}.{lang}.json"
        existing = load_json(target_file)["strings"] if target_file.exists() else {}
        for key, text in en.items():
            seg_id = f"strings/{hub}#{key}"
            if text and needs_translation(seg_id, text, existing.get(key), state):
                hint = STRING_FILE_HINTS.get(hub, f"UI/article text for the '{hub}' topic hub")
                out.append(Segment(seg_id, text, f"strings/{hub}", f"{hint}, key {key}"))
    return out


def write_hub_strings(lang: str, results: dict[str, str]) -> int:
    by_hub: dict[str, dict[str, str]] = {}
    for seg_id, text in results.items():
        if seg_id.startswith("strings/"):
            hub, key = seg_id.removeprefix("strings/").split("#", 1)
            by_hub.setdefault(hub, {})[key] = text
    for hub, values in by_hub.items():
        en_doc = load_json(CONTENT / "strings" / f"{hub}.en.json")
        target_file = CONTENT / "strings" / f"{hub}.{lang}.json"
        if target_file.exists():
            doc = load_json(target_file)
        else:
            doc = {"schemaVersion": en_doc["schemaVersion"], "hubId": en_doc["hubId"], "lang": lang,
                   "contentVersion": en_doc["contentVersion"], "translationStatus": "MACHINE_DRAFT",
                   "strings": {}}
        merged = dict(doc["strings"])
        merged.update(values)
        # Keep the English key order so diffs read naturally.
        doc["strings"] = {k: merged[k] for k in en_doc["strings"] if k in merged} | \
                         {k: v for k, v in merged.items() if k not in en_doc["strings"]}
        doc["contentVersion"] = max(doc.get("contentVersion", 0), en_doc["contentVersion"])
        if doc.get("translationStatus") == "COMPLETE":
            doc["translationStatus"] = "MACHINE_DRAFT"
        write_json(target_file, doc)
    return len(by_hub)


def sync_gallery_source() -> None:
    """Mirror MiracleGalleries.kt's English captions into galleries.en.json (the Kotlin is the source)."""
    captions = [kotlin_unescape(c) for c in re.findall(r'^\s+caption = "((?:[^"\\]|\\.)*)",$',
                                                       GALLERY_KT.read_text(encoding="utf-8"), re.M)]
    strings = {f"screen.gallery.{c}": c for c in dict.fromkeys(captions) if c.strip()}
    path = CONTENT / "strings" / "galleries.en.json"
    doc = {"schemaVersion": 1, "hubId": "galleries", "lang": "en", "contentVersion": 1,
           "translationStatus": "COMPLETE", "strings": strings}
    if not path.exists() or load_json(path)["strings"] != strings:
        write_json(path, doc)
        print(f"galleries.en.json: {len(strings)} captions")


# Entities ----------------------------------------------------------------

def entity_segments(lang: str, state: dict[str, str]) -> list[Segment]:
    out = []
    for etype in ENTITY_TYPES:
        for path in sorted((CONTENT / etype).glob("*.json")):
            doc = load_json(path)
            en = doc.get("text", {}).get("en")
            if not en:
                continue
            existing = doc["text"].get(lang) or {}
            for fld, text in en.items():
                if not isinstance(text, str) or not text.strip():
                    continue
                seg_id = f"{etype}/{path.stem}#{fld}"
                if needs_translation(seg_id, text, existing.get(fld), state):
                    out.append(Segment(seg_id, text, f"{etype}/{path.stem}",
                                       f"field '{fld}' of {ENTITY_HINTS[etype]} ({path.stem})"))
    return out


def write_entities(lang: str, results: dict[str, str], state: dict[str, str]) -> int:
    by_file: dict[str, dict[str, str]] = {}
    for seg_id, text in results.items():
        etype = seg_id.split("/", 1)[0]
        if etype in ENTITY_TYPES:
            stem, fld = seg_id.split("#", 1)
            by_file.setdefault(stem, {})[fld] = text
    for stem, fields in by_file.items():
        path = CONTENT / f"{stem}.json"
        doc = load_json(path)
        en = doc["text"]["en"]
        current = dict(doc["text"].get(lang) or {})
        current.update(fields)
        # Same fields, same order as English. A field that failed this run gets
        # the English text so the entry still loads, and is marked pending in the
        # state so the next run retranslates it instead of mistaking it for human.
        for k, v in en.items():
            if k not in current and v is not None:
                current[k] = v
                state[f"{stem}#{k}"] = "pending"
        doc["text"][lang] = {k: current.get(k) for k in en}
        write_json(path, doc)
    return len(by_file)


# UI chrome ---------------------------------------------------------------

KOTLIN_STRING_VAR = re.compile(r'^    var (\w+): String = "((?:[^"\\]|\\.)*)"\s*$', re.M)
KOTLIN_PAIR = re.compile(r'^\s*"((?:[^"\\]|\\.)*)" to "((?:[^"\\]|\\.)*)",?\s*$', re.M)


def kotlin_unescape(s: str) -> str:
    return re.sub(r'\\(.)', lambda m: {"n": "\n", "t": "\t"}.get(m.group(1), m.group(1)), s)


def kotlin_escape(s: str) -> str:
    return (s.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$")
             .replace("\n", "\\n").replace("\t", "\\t"))


def ui_source() -> dict[str, str]:
    return {name: kotlin_unescape(v) for name, v in KOTLIN_STRING_VAR.findall((I18N / "Strings.kt").read_text())}


def vestment_source() -> list[str]:
    return [kotlin_unescape(k) for k, _ in KOTLIN_PAIR.findall((I18N / "VestmentStringsIT.kt").read_text())]


def ui_cache_path(lang: str) -> Path:
    return STATE_DIR / f"ui.{lang}.json"


def ui_segments(lang: str, state: dict[str, str]) -> list[Segment]:
    if lang in HANDWRITTEN_UI:
        return []
    cache = load_json(ui_cache_path(lang)) if ui_cache_path(lang).exists() else {}
    out = []
    for name, text in ui_source().items():
        seg_id = f"ui#{name}"
        if text and needs_translation(seg_id, text, cache.get(seg_id), state):
            out.append(Segment(seg_id, text, "ui", f"app interface label `{name}`"))
    for text in vestment_source():
        seg_id = f"vest#{sha(text)}"
        if needs_translation(seg_id, text, cache.get(seg_id), state):
            out.append(Segment(seg_id, text, "vestments", "Sacred Vestments reference entry"))
    return out


def write_ui(lang: str, results: dict[str, str]) -> None:
    if lang in HANDWRITTEN_UI:
        return
    cache = load_json(ui_cache_path(lang)) if ui_cache_path(lang).exists() else {}
    cache.update({k: v for k, v in results.items() if k.startswith(("ui#", "vest#"))})
    STATE_DIR.mkdir(parents=True, exist_ok=True)
    write_json(ui_cache_path(lang), dict(sorted(cache.items())))
    generate_kotlin(lang, cache)


def generate_kotlin(lang: str, cache: dict[str, str]) -> None:
    upper = lang.upper()
    name = LANGUAGES[lang]
    lines = [
        "package com.ynotlabs.cathopedia.i18n",
        "",
        f"// GENERATED by tools/translate/translate.py — do not edit by hand.",
        f"// Machine translation (MACHINE_DRAFT); fix wording in tools/translate/state/ui.{lang}.json",
        "// or the glossary, then re-run the tool.",
        "",
        f"/** {name} UI chrome. Any field missing here falls back to English. */",
        f"val {upper}: Strings = Strings().apply {{",
    ]
    for field_name, _ in ui_source().items():
        value = cache.get(f"ui#{field_name}")
        if value is not None:
            lines.append(f'    {field_name} = "{kotlin_escape(value)}"')
    lines.append(f"    vestmentTranslations = VESTMENT_TRANSLATIONS_{upper}")
    lines.append("}")
    (I18N / f"Strings{upper}.kt").write_text("\n".join(lines) + "\n", encoding="utf-8")

    vest = [
        "package com.ynotlabs.cathopedia.i18n",
        "",
        "// GENERATED by tools/translate/translate.py — do not edit by hand.",
        "",
        f"/** {name} translations for the structured Sacred Vestments reference content. */",
        f"internal val VESTMENT_TRANSLATIONS_{upper}: Map<String, String> = mapOf(",
    ]
    for text in vestment_source():
        value = cache.get(f"vest#{sha(text)}")
        if value is not None:
            vest.append(f'    "{kotlin_escape(text)}" to "{kotlin_escape(value)}",')
    vest.append(")")
    (I18N / f"VestmentStrings{upper}.kt").write_text("\n".join(vest) + "\n", encoding="utf-8")


# ---------------------------------------------------------------- prompting

def system_prompt(lang: str) -> str:
    name = LANGUAGES[lang]
    glossary_file = GLOSSARY_DIR / f"{lang}.md"
    glossary = glossary_file.read_text(encoding="utf-8").strip() if glossary_file.exists() else ""
    common = (GLOSSARY_DIR / "common.md").read_text(encoding="utf-8").strip() \
        if (GLOSSARY_DIR / "common.md").exists() else ""
    return f"""You translate Cathopedia, a Catholic reference app, from English into {name}.

Readers are Catholics and curious newcomers reading on a phone. The text must read
as if it were written in {name} by a careful Catholic editor, not translated.

Terminology: use the vocabulary the Catholic Church itself uses in {name} — the
official {name} editions of the Catechism of the Catholic Church, the Roman Missal
and the documents on vatican.va, and the usage of the {name}-speaking bishops'
conferences. Prefer Catholic over Protestant renderings where they differ.

Names: saints, popes, apostles, biblical figures, religious orders and places take
their customary {name} forms (popes by their {name} regnal names). When a text
quotes Scripture or a well-known prayer, use the established {name} liturgical
wording rather than translating the English afresh. Write Scripture references in
the {name} customary style (book abbreviations and chapter/verse separator).
Latin phrases conventionally left in Latin (Agnus Dei, Ex cathedra, Magisterium as
a term of art, motu proprio…) stay in Latin. "Cathopedia" is never translated.

Format: each item is one string from the app. Keep every placeholder such as
{{count}} or {{name}} exactly as written. Keep Markdown exactly: **bold**, *italic*,
[link text](url) (translate the text, never the url), headings, list markers and
line breaks. Keep the register — a two-word button label stays short, a heading stays
a heading. Do not add notes, explanations or quotation marks around items. Never
leave an item untranslated unless it is a proper name or Latin that stays as is.

Answer with every item id you were given, each exactly once.
{common}
{glossary}""".strip()


def user_prompt(segments: list[Segment]) -> str:
    items = [{"id": s.id, "context": s.hint, "text": s.source} for s in segments]
    return "Translate these items:\n\n" + json.dumps(items, ensure_ascii=False, indent=1)


OUTPUT_SCHEMA = {
    "type": "object",
    "properties": {
        "items": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {"id": {"type": "string"}, "text": {"type": "string"}},
                "required": ["id", "text"],
                "additionalProperties": False,
            },
        }
    },
    "required": ["items"],
    "additionalProperties": False,
}


def request_params(lang: str, segments: list[Segment]) -> dict:
    words = sum(len(s.source.split()) for s in segments)
    return {
        "model": MODEL,
        # Tamil runs ~3x English in tokens; leave room for thinking as well.
        "max_tokens": min(64000, 8000 + words * (12 if lang == "ta" else 5)),
        "system": [{"type": "text", "text": system_prompt(lang), "cache_control": {"type": "ephemeral"}}],
        "messages": [{"role": "user", "content": user_prompt(segments)}],
        "output_config": {"format": {"type": "json_schema", "schema": OUTPUT_SCHEMA}},
    }


def chunk(segments: list[Segment]) -> list[list[Segment]]:
    chunks: list[list[Segment]] = []
    current: list[Segment] = []
    words = 0
    for seg in segments:
        w = len(seg.source.split())
        if current and (seg.group != current[-1].group and words > MAX_WORDS_PER_REQUEST // 3
                        or len(current) >= MAX_ITEMS_PER_REQUEST
                        or words + w > MAX_WORDS_PER_REQUEST):
            chunks.append(current)
            current, words = [], 0
        current.append(seg)
        words += w
    if current:
        chunks.append(current)
    return chunks


# ---------------------------------------------------------------- validation

def problems(source: str, text: str) -> list[str]:
    errs = []
    if not text.strip():
        errs.append("empty")
    if sorted(PLACEHOLDER.findall(source)) != sorted(PLACEHOLDER.findall(text)):
        errs.append("placeholders differ")
    for marker in ("**", "](", "\n\n"):
        if source.count(marker) != text.count(marker):
            errs.append(f"markdown '{marker!r}' count differs")
    if source.count("\n") and not text.count("\n"):
        errs.append("line breaks lost")
    return errs


def parse_answer(message, segments: list[Segment]) -> tuple[dict[str, str], list[Segment], list[str]]:
    """Returns (accepted translations, segments to retry, log lines)."""
    by_id = {s.id: s for s in segments}
    if message.stop_reason == "refusal":
        return {}, segments, [f"refused ({len(segments)} items)"]
    if message.stop_reason == "max_tokens":
        return {}, segments, [f"hit max_tokens ({len(segments)} items)"]
    text = next((b.text for b in message.content if b.type == "text"), "")
    try:
        items = json.loads(text)["items"]
    except (json.JSONDecodeError, KeyError) as e:
        return {}, segments, [f"unparseable answer: {e}"]
    accepted, log = {}, []
    for item in items:
        seg = by_id.get(item.get("id"))
        if seg is None or seg.id in accepted:
            continue
        errs = problems(seg.source, item["text"])
        if errs:
            log.append(f"{seg.id}: {', '.join(errs)}")
        else:
            accepted[seg.id] = item["text"]
    retry = [s for s in segments if s.id not in accepted]
    missing = [s.id for s in retry if s.id not in {i.get("id") for i in items}]
    if missing:
        log.append(f"{len(missing)} ids missing from answer")
    return accepted, retry, log


# ---------------------------------------------------------------- running

def client():
    try:
        import anthropic
    except ImportError:
        sys.exit("The anthropic SDK is not installed: pip install anthropic")
    return anthropic.Anthropic()


def run_sync(c, lang: str, segments: list[Segment]) -> tuple[dict[str, str], list[Segment]]:
    """One request per chunk, with the server-side fallback on a refusal."""
    done, failed = {}, []
    for i, part in enumerate(chunk(segments), 1):
        params = request_params(lang, part)
        with c.beta.messages.stream(**params, betas=["server-side-fallback-2026-07-01"],
                                    fallbacks="default") as stream:
            message = stream.get_final_message()
        accepted, retry, log = parse_answer(message, part)
        done.update(accepted)
        failed.extend(retry)
        print(f"  [{lang}] sync {i}: {len(accepted)}/{len(part)} ok" + (f" — {'; '.join(log[:3])}" if log else ""))
    return done, failed


def submit_batch(c, jobs: list[LangJob]) -> str:
    from anthropic.types.message_create_params import MessageCreateParamsNonStreaming
    from anthropic.types.messages.batch_create_params import Request

    requests, manifest = [], {}
    for job in jobs:
        for i, part in enumerate(chunk(job.segments)):
            custom_id = f"{job.lang}-{i:05d}"
            manifest[custom_id] = {"lang": job.lang, "ids": [s.id for s in part]}
            requests.append(Request(custom_id=custom_id,
                                    params=MessageCreateParamsNonStreaming(**request_params(job.lang, part))))
    batch = c.messages.batches.create(requests=requests)
    STATE_DIR.mkdir(parents=True, exist_ok=True)
    write_json(PENDING_FILE, {"batchId": batch.id, "manifest": manifest})
    print(f"Submitted batch {batch.id} with {len(requests)} requests.")
    return batch.id


def collect_batch(c, segments_by_lang: dict[str, dict[str, Segment]]) -> tuple[dict[str, dict[str, str]], dict[str, list[Segment]]]:
    pending = load_json(PENDING_FILE)
    batch_id, manifest = pending["batchId"], pending["manifest"]
    while True:
        batch = c.messages.batches.retrieve(batch_id)
        if batch.processing_status == "ended":
            break
        counts = batch.request_counts
        print(f"  batch {batch_id}: {counts.processing} processing, {counts.succeeded} done — checking again in 60s")
        time.sleep(60)

    done: dict[str, dict[str, str]] = {}
    failed: dict[str, list[Segment]] = {}
    for result in c.messages.batches.results(batch_id):
        entry = manifest[result.custom_id]
        lang = entry["lang"]
        lang_segments = segments_by_lang.setdefault(lang, {})
        part = [lang_segments[i] for i in entry["ids"] if i in lang_segments]
        if result.result.type == "succeeded":
            accepted, retry, log = parse_answer(result.result.message, part)
            if log:
                print(f"  [{result.custom_id}] {'; '.join(log[:3])}")
        else:
            accepted, retry = {}, part
            print(f"  [{result.custom_id}] {result.result.type}")
        done.setdefault(lang, {}).update(accepted)
        failed.setdefault(lang, []).extend(retry)
    return done, failed


def bump_content_version() -> None:
    src = CONTENT_LOADER.read_text()
    m = re.search(r'CONTENT_VERSION = "(\d+)"', src)
    if m:
        new = str(int(m.group(1)) + 1)
        CONTENT_LOADER.write_text(src.replace(m.group(0), f'CONTENT_VERSION = "{new}"'))
        print(f"CONTENT_VERSION -> {new}")


def apply_results(lang: str, results: dict[str, str], segments: dict[str, Segment]) -> None:
    if not results:
        return
    state = load_state(lang)
    for seg_id in results:
        state[seg_id] = sha(segments[seg_id].source)
    files = write_hub_strings(lang, results) + write_entities(lang, results, state)
    write_ui(lang, results)
    save_state(lang, state)
    print(f"  [{lang}] wrote {len(results)} translations into {files} content files"
          + ("" if lang in HANDWRITTEN_UI else " + UI tables"))


def collect_segments(lang: str, only: set[str]) -> list[Segment]:
    state = load_state(lang)
    segs: list[Segment] = []
    if "strings" in only:
        segs += hub_string_segments(lang, state)
    if "entities" in only:
        segs += entity_segments(lang, state)
    if "ui" in only:
        segs += ui_segments(lang, state)
    return segs


# ---------------------------------------------------------------- offline (no API)
#
# --export writes the pending segments as chunk files; a person (or a Claude
# session on a subscription plan) writes a matching <name>.out.json holding
# {"<id>": "<translation>", ...}; --import validates and applies them exactly as
# an API run would, records state, and moves the pair into work/done/.

WORK_DIR = HERE / "work"


def export_work(jobs: list[LangJob]) -> None:
    WORK_DIR.mkdir(parents=True, exist_ok=True)
    for job in jobs:
        (WORK_DIR / f"{job.lang}-instructions.md").write_text(system_prompt(job.lang) + "\n", encoding="utf-8")
        existing = {p.name.split(".")[0] for p in WORK_DIR.glob(f"{job.lang}-*.in.json")}
        exported = {i for p in WORK_DIR.glob(f"{job.lang}-*.in.json") for i in (x["id"] for x in load_json(p))}
        todo = [s for s in job.segments if s.id not in exported]
        n = len(existing)
        for part in chunk(todo):
            name = f"{job.lang}-{n:04d}"
            write_json(WORK_DIR / f"{name}.in.json",
                       [{"id": s.id, "context": s.hint, "text": s.source} for s in part])
            n += 1
        print(f"{job.lang}: exported {len(todo)} segments into {n - len(existing)} files under {WORK_DIR}")


def import_work(langs: list[str]) -> None:
    done_dir = WORK_DIR / "done"
    applied_any = False
    for lang in langs:
        outs = sorted(WORK_DIR.glob(f"{lang}-*.out.json"))
        if not outs:
            continue
        segments = {s.id: s for s in collect_segments(lang, {"strings", "entities", "ui"})}
        accepted: dict[str, str] = {}
        rejected = 0
        for out in outs:
            answers = load_json(out)
            bad = []
            for seg_id, text in answers.items():
                seg = segments.get(seg_id)
                if seg is None:
                    continue  # already translated, or the English key is gone
                errs = problems(seg.source, text)
                if errs:
                    bad.append(f"{seg_id}: {', '.join(errs)}")
                else:
                    accepted[seg_id] = text
            rejected += len(bad)
            for line in bad[:5]:
                print(f"  [{out.name}] {line}")
            if not bad:
                done_dir.mkdir(parents=True, exist_ok=True)
                stem = out.name.removesuffix(".out.json")
                out.rename(done_dir / out.name)
                src = WORK_DIR / f"{stem}.in.json"
                if src.exists():
                    src.rename(done_dir / src.name)
        apply_results(lang, accepted, segments)
        applied_any = applied_any or bool(accepted)
        print(f"{lang}: imported {len(accepted)}, rejected {rejected} (files with rejections stay in work/)")
    if applied_any:
        bump_content_version()


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--lang", nargs="+", choices=sorted(LANGUAGES), help="target languages")
    ap.add_argument("--all", action="store_true", help="every language in LANGUAGES")
    ap.add_argument("--only", default="strings,entities,ui", help="comma list of: strings,entities,ui")
    ap.add_argument("--limit", type=int, help="translate at most N segments per language (for a trial run)")
    ap.add_argument("--dry-run", action="store_true", help="count and estimate, call nothing")
    ap.add_argument("--sync", action="store_true", help="synchronous requests instead of the Batches API")
    ap.add_argument("--resume", action="store_true", help="collect the batch recorded in state/pending_batch.json")
    ap.add_argument("--stubs", action="store_true", help="(re)generate StringsXX.kt from the UI cache only")
    ap.add_argument("--export", action="store_true",
                    help="write pending segments to work/<lang>-NNNN.in.json for offline translation (no API)")
    ap.add_argument("--import", dest="import_", action="store_true",
                    help="apply work/<lang>-NNNN.out.json files ({id: text}) with the same validation")
    args = ap.parse_args()

    langs = sorted(LANGUAGES) if args.all else (args.lang or [])
    only = set(args.only.split(","))

    if args.import_:
        import_work(langs)
        return

    if args.stubs:
        for lang in langs:
            if lang not in HANDWRITTEN_UI:
                cache = load_json(ui_cache_path(lang)) if ui_cache_path(lang).exists() else {}
                generate_kotlin(lang, cache)
                print(f"generated Strings{lang.upper()}.kt ({len(cache)} cached values)")
        return

    if args.resume:
        if not PENDING_FILE.exists():
            sys.exit("No pending batch.")
        manifest = load_json(PENDING_FILE)["manifest"]
        resume_langs = sorted({e["lang"] for e in manifest.values()})
        langs = resume_langs

    sync_gallery_source()
    jobs = []
    for lang in langs:
        segs = collect_segments(lang, only)
        if args.limit:
            segs = segs[: args.limit]
        jobs.append(LangJob(lang, segs))

    total_in = total_out = 0
    for job in jobs:
        words = sum(len(s.source.split()) for s in job.segments)
        tok_in = int(words * 1.4) + len(chunk(job.segments)) * 900
        tok_out = int(words * (4.0 if job.lang == "ta" else 1.9)) * 2  # x2 for thinking
        total_in += tok_in
        total_out += tok_out
        kinds = {}
        for s in job.segments:
            k = s.id.split("#")[0].split("/")[0]
            kinds[k] = kinds.get(k, 0) + 1
        print(f"{job.lang}: {len(job.segments)} segments, {words:,} words, "
              f"{len(chunk(job.segments))} requests  {dict(sorted(kinds.items()))}")
    cost = total_in / 1e6 * BATCH_PRICE_IN + total_out / 1e6 * BATCH_PRICE_OUT
    print(f"Estimated batch cost: ~${cost:,.0f} (sync is 2x). Rough: thinking length varies.")
    if args.dry_run:
        return
    if args.export:
        export_work(jobs)
        return

    c = client()
    segments_by_lang = {job.lang: {s.id: s for s in job.segments} for job in jobs}
    if args.sync:
        for job in jobs:
            if not job.segments:
                continue
            done, failed = run_sync(c, job.lang, job.segments)
            apply_results(job.lang, done, segments_by_lang[job.lang])
            if failed:
                print(f"  [{job.lang}] {len(failed)} items still untranslated; re-run to retry them.")
    else:
        if not args.resume:
            if not any(job.segments for job in jobs):
                print("Nothing to translate.")
                return
            submit_batch(c, [j for j in jobs if j.segments])
        done, failed = collect_batch(c, segments_by_lang)
        for lang, results in done.items():
            apply_results(lang, results, segments_by_lang[lang])
        PENDING_FILE.unlink(missing_ok=True)
        # Refusals, errors and validation failures get one synchronous retry with
        # the server-side fallback (not available on the Batches API).
        for lang, segs in failed.items():
            if segs:
                print(f"  [{lang}] retrying {len(segs)} items synchronously")
                redone, still = run_sync(c, lang, segs)
                apply_results(lang, redone, segments_by_lang[lang])
                if still:
                    print(f"  [{lang}] {len(still)} items still untranslated; re-run to retry them.")
    bump_content_version()
    print("Next: ./gradlew :shared:compileContent, then build.")


if __name__ == "__main__":
    main()
