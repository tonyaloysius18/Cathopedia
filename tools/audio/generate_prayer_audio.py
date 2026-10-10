#!/usr/bin/env python3
"""Recorded prayer audio for Cathopedia's read-aloud player (phase 2).

Turns every prayer in content/prayers/ into natural speech with Google Cloud
Text-to-Speech, one recording per part (heading, stanza; the title is not read), exactly the
parts the app reads (speech/PrayerSpeechScript.kt). Each language becomes one
pack the app downloads on first play:

    dist/<lang>-<version>.pack   the MP3 parts, back to back
    dist/<lang>-<version>.json   {"units": {key: [offset, length]}}

and shared/.../files/prayer_audio/packs.json tells the app which packs exist.
A part's key is FNV-1a 64 of "<language>\\n<text>" (speech/SpeechKey.kt), so
parts whose text changed later fall back to the device voice until the next
run.

Usage:
    python3 tools/audio/generate_prayer_audio.py --dry-run         # counts, cost, no key
    python3 tools/audio/generate_prayer_audio.py --lang la ta      # generate + pack
    python3 tools/audio/generate_prayer_audio.py --all
    python3 tools/audio/generate_prayer_audio.py --upload          # publish dist/ to the release

The API key is read from tools/audio/google-tts.key (gitignored) or the
GOOGLE_TTS_API_KEY environment variable, and is never printed.
"""
from __future__ import annotations

import argparse
import base64
import glob
import hashlib
import json
import os
import re
import subprocess
import sys
import time
import urllib.error
import urllib.request

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
HERE = os.path.join(ROOT, "tools", "audio")
CACHE = os.path.join(HERE, "cache")
DIST = os.path.join(HERE, "dist")
STATE = os.path.join(HERE, "state.json")
PACKS_JSON = os.path.join(ROOT, "shared/src/commonMain/composeResources/files/prayer_audio/packs.json")
FIXTURE = os.path.join(ROOT, "shared/src/commonTest/kotlin/com/ynotlabs/cathopedia/speech/PrayerAudioParityFixture.kt")
RELEASE_TAG = "prayer-audio"
REPO = "tonyaloysius18/Cathopedia"

LANGS = ["en", "la", "fr", "it", "es", "pt", "de", "nl", "pl", "ta"]

# Same accents as SpeechVoices.localeFor; Latin is read by an Italian voice.
LOCALE = {"en": "en-US", "la": "it-IT", "fr": "fr-FR", "it": "it-IT", "es": "es-ES",
          "pt": "pt-BR", "de": "de-DE", "nl": "nl-NL", "pl": "pl-PL", "ta": "ta-IN"}

# One calm voice identity across every language (Chirp 3 HD names are shared by locale).
DEFAULT_PERSONA = "Charon"
SPEAKING_RATE = 0.92          # a little slower than conversation, as for the device voice
MAX_REQUEST_BYTES = 4500      # stay under the API's per-request input limit
CHIRP_PRICE_PER_MILLION = 30.0
FREE_CHARS_PER_MONTH = 1_000_000


# --- The app's part splitting, ported line for line (keep in sync with Kotlin) ---------

def kt_lines(s: str) -> list[str]:
    return re.split(r"\r\n|\n|\r", s)


def split_sections(body: str) -> list[tuple[str | None, str]]:
    """PrayerDetailScreen.splitPrayerSections"""
    if "#" not in body:
        return [(None, body)]
    sections, title, current = [], None, []
    for line in kt_lines(body):
        if line.strip().startswith("#"):
            if current or title is not None:
                sections.append((title, "".join(current).strip()))
            title = line.strip().lstrip("#").strip()
            current = []
        else:
            current.append(line + "\n")
    if current or title is not None:
        sections.append((title, "".join(current).strip()))
    return sections


def paragraphs(body: str) -> list[str]:
    """PrayerSpeechScript.prayerParagraphs"""
    return [p for p in re.split(r"\n\s*\n", body.strip(), flags=re.ASCII) if p.strip()]


VERSICLE = re.compile(r"^(?:[VR]\.|[℣℟])\s*", re.ASCII)
LIST_MARK = re.compile(r"^(?:[-•]\s+)", re.ASCII)


def clean(line: str) -> str:
    """PrayerSpeechScript.cleanForSpeech"""
    s = line.strip()
    if s.startswith(">"):
        s = s[1:]
    s = s.strip()
    s = LIST_MARK.sub("", s, count=1)
    s = VERSICLE.sub("", s, count=1)
    s = s.replace("**", "").replace("*", "").replace("_", " ")
    return s.lstrip("#").strip()


def speech_units(body: str) -> list[str]:
    """PrayerSpeechScript.prayerSpeechScript (texts only; the prayer's title is not read)"""
    units = []
    for heading, section in split_sections(body):
        if heading is not None:
            h = clean(heading)
            if h:
                units.append(h)
        for p in paragraphs(section):
            text = "\n".join(c for c in (clean(l) for l in kt_lines(p)) if c.strip())
            if text.strip():
                units.append(text)
    return units


def speech_key(language: str, text: str) -> str:
    """SpeechKey.speechKey"""
    h = 0xcbf29ce484222325
    for b in f"{language}\n{text}".encode("utf-8"):
        h ^= b
        h = (h * 0x100000001b3) & 0xFFFFFFFFFFFFFFFF
    return f"{h:016x}"


# --- Content ---------------------------------------------------------------------------

def load_units() -> dict[str, dict[str, str]]:
    """lang -> {key: text}, de-duplicated (the same stanza in many prayers is recorded once)."""
    out: dict[str, dict[str, str]] = {l: {} for l in LANGS}
    for path in sorted(glob.glob(os.path.join(ROOT, "content/prayers/*.json"))):
        prayer = json.load(open(path, encoding="utf-8"))
        for lang, text in prayer.get("text", {}).items():
            if lang not in out:
                continue
            for unit in speech_units(text.get("bodyMd") or ""):
                out[lang][speech_key(lang, unit)] = unit
    return out


# --- Google Cloud Text-to-Speech -------------------------------------------------------

def api_key() -> str:
    key = os.environ.get("GOOGLE_TTS_API_KEY")
    path = os.path.join(HERE, "google-tts.key")
    if not key and os.path.exists(path):
        key = open(path).read().strip()
    if not key:
        sys.exit("No API key: put it in tools/audio/google-tts.key or GOOGLE_TTS_API_KEY.")
    return key


def call(url: str, body: dict | None = None) -> dict:
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json"})
    for attempt in range(5):
        try:
            with urllib.request.urlopen(req, timeout=120) as r:
                return json.load(r)
        except urllib.error.HTTPError as e:
            detail = e.read().decode(errors="replace")
            if e.code in (429, 500, 503) and attempt < 4:
                time.sleep(2 ** attempt * 2)
                continue
            # Never echo the URL: it carries the key.
            raise RuntimeError(f"HTTP {e.code}: {detail[:400]}") from None
    raise RuntimeError("unreachable")


def pick_voice(key: str, locale: str, persona: str) -> str:
    voices = call(f"https://texttospeech.googleapis.com/v1/voices?languageCode={locale}&key={key}").get("voices", [])
    names = [v["name"] for v in voices if locale in v.get("languageCodes", [])]
    for wanted in (f"{locale}-Chirp3-HD-{persona}",):
        if wanted in names:
            return wanted
    for family in ("Chirp3-HD", "Chirp-HD", "Neural2", "Wavenet"):
        found = sorted(n for n in names if f"-{family}-" in n)
        if found:
            return found[0]
    if names:
        return sorted(names)[0]
    raise RuntimeError(f"No Google voice for {locale}")


def chunks(text: str) -> list[str]:
    """Splits a long part on line breaks so each request stays under the size limit."""
    if len(text.encode()) <= MAX_REQUEST_BYTES:
        return [text]
    out, current = [], ""
    for line in text.split("\n"):
        candidate = f"{current}\n{line}" if current else line
        if current and len(candidate.encode()) > MAX_REQUEST_BYTES:
            out.append(current)
            current = line
        else:
            current = candidate
    if current:
        out.append(current)
    return out


def synthesize(key: str, voice: str, locale: str, text: str, use_rate: list[bool]) -> bytes:
    audio = b""
    for part in chunks(text):
        cfg = {"audioEncoding": "MP3", "sampleRateHertz": 24000}
        if use_rate[0]:
            cfg["speakingRate"] = SPEAKING_RATE
        body = {"input": {"text": part}, "voice": {"languageCode": locale, "name": voice}, "audioConfig": cfg}
        try:
            r = call(f"https://texttospeech.googleapis.com/v1/text:synthesize?key={key}", body)
        except RuntimeError as e:
            if use_rate[0] and "speaking" in str(e).lower():
                use_rate[0] = False   # this voice family ignores pace control
                return synthesize(key, voice, locale, text, use_rate)
            raise
        audio += base64.b64decode(r["audioContent"])
    return audio


# --- Packs -----------------------------------------------------------------------------

def read_json(path: str, default):
    return json.load(open(path, encoding="utf-8")) if os.path.exists(path) else default


def build(langs: list[str], persona: str) -> None:
    key = api_key()
    units = load_units()
    state = read_json(STATE, {"packs": {}})
    packs = read_json(PACKS_JSON, {"baseUrl": "", "packs": {}})
    packs["baseUrl"] = f"https://github.com/{REPO}/releases/download/{RELEASE_TAG}"
    os.makedirs(DIST, exist_ok=True)

    for lang in langs:
        locale = LOCALE[lang]
        voice = pick_voice(key, locale, persona)
        cache = os.path.join(CACHE, voice)
        os.makedirs(cache, exist_ok=True)
        todo = {k: t for k, t in units[lang].items() if not os.path.exists(os.path.join(cache, f"{k}.mp3"))}
        print(f"{lang}: voice {voice}, {len(units[lang])} parts, {len(todo)} to synthesize")
        use_rate = [True]
        for i, (k, text) in enumerate(sorted(todo.items()), 1):
            audio = synthesize(key, voice, locale, text, use_rate)
            tmp = os.path.join(cache, f"{k}.mp3.tmp")
            open(tmp, "wb").write(audio)
            os.replace(tmp, os.path.join(cache, f"{k}.mp3"))
            if i % 25 == 0 or i == len(todo):
                print(f"  {lang}: {i}/{len(todo)}", flush=True)

        # The pack: parts back to back, sorted by key so identical input gives identical bytes.
        index, blob = {}, bytearray()
        for k in sorted(units[lang]):
            audio = open(os.path.join(cache, f"{k}.mp3"), "rb").read()
            index[k] = [len(blob), len(audio)]
            blob += audio
        digest = hashlib.sha256(blob).hexdigest()
        previous = state["packs"].get(lang, {})
        version = previous.get("version", 0)
        if previous.get("sha256") != digest:
            version += 1
        for old in glob.glob(os.path.join(DIST, f"{lang}-*")):
            os.remove(old)
        open(os.path.join(DIST, f"{lang}-{version}.pack"), "wb").write(blob)
        json.dump({"units": index}, open(os.path.join(DIST, f"{lang}-{version}.json"), "w"), separators=(",", ":"))
        state["packs"][lang] = {"version": version, "sha256": digest, "voice": voice, "bytes": len(blob)}
        packs["packs"][lang] = {"version": version, "bytes": len(blob), "voice": voice}
        print(f"  {lang}: pack v{version}, {len(blob) / 1e6:.1f} MB")

    json.dump(state, open(STATE, "w"), indent=2, sort_keys=True)
    packs["packs"] = dict(sorted(packs["packs"].items()))
    json.dump(packs, open(PACKS_JSON, "w"), indent=2)
    open(PACKS_JSON, "a").write("\n")
    print("Wrote", os.path.relpath(PACKS_JSON, ROOT), "- run --upload, then ship the app with it.")


def upload() -> None:
    files = sorted(glob.glob(os.path.join(DIST, "*")))
    if not files:
        sys.exit("dist/ is empty: build first.")
    exists = subprocess.run(["gh", "release", "view", RELEASE_TAG, "--repo", REPO], capture_output=True).returncode == 0
    if not exists:
        subprocess.run(["gh", "release", "create", RELEASE_TAG, "--repo", REPO, "--title", "Prayer audio",
                        "--notes", "Recorded prayer audio packs for the Cathopedia read-aloud player.",
                        "--latest=false"], check=True)
    subprocess.run(["gh", "release", "upload", RELEASE_TAG, *files, "--repo", REPO, "--clobber"], check=True)
    print(f"Uploaded {len(files)} files to {RELEASE_TAG}.")


# --- Dry run + parity fixture -----------------------------------------------------------

def dry_run() -> None:
    units = load_units()
    total = 0
    for lang in LANGS:
        chars = sum(len(t) for t in units[lang].values())
        total += chars
        print(f"{lang}: {len(units[lang]):5d} parts, {chars:7d} chars, ~{chars / 900:4.0f} min")
    paid = max(0, total - FREE_CHARS_PER_MONTH)
    print(f"total {total} chars; Chirp 3 HD cost ~${paid / 1e6 * CHIRP_PRICE_PER_MILLION:.2f} "
          f"after the {FREE_CHARS_PER_MONTH:,} free monthly chars")


def kt_string(s: str) -> str:
    return '"' + s.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$").replace("\n", "\\n").replace("\r", "\\r") + '"'


def write_fixture() -> None:
    """Real prayers in every language, so a Kotlin test proves both sides cut the same parts."""
    wanted = ["our-father", "angelus", "seven-sorrows", "chaplet-of-divine-mercy", "memorare"]
    cases = []
    for pid in wanted:
        path = os.path.join(ROOT, f"content/prayers/{pid}.json")
        if not os.path.exists(path):
            continue
        prayer = json.load(open(path, encoding="utf-8"))
        for lang, text in prayer["text"].items():
            title = text.get("title") or pid.replace("-", " ").title()
            body = text.get("bodyMd") or ""
            keys = [speech_key(lang, u) for u in speech_units(body)]
            cases.append((lang, title, body, keys))
    lines = [
        "package com.ynotlabs.cathopedia.speech",
        "",
        "// GENERATED by tools/audio/generate_prayer_audio.py --fixture. Do not edit.",
        "// Real prayers with the part keys the audio generator computed for them.",
        "internal val PrayerAudioParityCases: List<ParityCase> = listOf(",
    ]
    for lang, title, body, keys in cases:
        lines.append(f"    ParityCase({kt_string(lang)}, {kt_string(title)}, {kt_string(body)}, listOf({', '.join(kt_string(k) for k in keys)})),")
    lines += [")", "", "internal data class ParityCase(val language: String, val title: String, val bodyMd: String, val keys: List<String>)", ""]
    open(FIXTURE, "w", encoding="utf-8").write("\n".join(lines))
    print(f"Wrote {len(cases)} cases to {os.path.relpath(FIXTURE, ROOT)}")


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--lang", nargs="+", choices=LANGS)
    ap.add_argument("--all", action="store_true")
    ap.add_argument("--persona", default=DEFAULT_PERSONA, help="Chirp 3 HD voice name, e.g. Charon, Kore, Aoede")
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--fixture", action="store_true", help="regenerate the Kotlin parity fixture")
    ap.add_argument("--upload", action="store_true")
    a = ap.parse_args()
    if a.dry_run:
        dry_run()
    if a.fixture:
        write_fixture()
    if a.all or a.lang:
        build(LANGS if a.all else a.lang, a.persona)
    if a.upload:
        upload()
    if not (a.dry_run or a.fixture or a.all or a.lang or a.upload):
        ap.print_help()


if __name__ == "__main__":
    main()
