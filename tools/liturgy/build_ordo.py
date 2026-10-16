#!/usr/bin/env python3
"""Build Cathopedia's offline Ordo (liturgical calendar + lectionary references).

Two open sources, both reproducible:

* Calendar: the Liturgical Calendar API (Apache-2.0,
  https://github.com/Liturgical-Calendar/LiturgicalCalendarAPI). Its engine applies the
  General Norms precedence, the 2002/2008 Missal sanctoral and later decrees, and national
  calendars. One request per profile/year; responses are cached under build/liturgy-cache.
* Lectionary references: the Tamil Catholic Lectionary reading list (public domain /
  Unlicense, https://github.com/jayarathina/Tamil-Catholic-Lectionary), with Tamil book
  sigla converted to the English abbreviations ScriptureCitationFormatter already expands.
  Only references (facts) are taken, never reading text.

Psalm verses are numbered as in the RSV/KJV tradition, without counting psalm titles (the US
NABRE and the Grail psalter count titles as verse 1, so their psalm verses often run one higher).
The licensed lectionary added in Phase 2 brings its own exact references and supersedes these.

Usage:  python3 tools/liturgy/build_ordo.py [--first 2025] [--last 2040]
"""

from __future__ import annotations

import argparse
import difflib
import json
import re
import time
import unicodedata
import urllib.request
from collections import OrderedDict
from datetime import date, timedelta
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CACHE = ROOT / "build" / "liturgy-cache"
OUT_DIR = ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "content" / "ordo"

API = "https://litcal.johnromanodorazio.com/api/dev"
TAMIL_LIST = ("https://raw.githubusercontent.com/jayarathina/Tamil-Catholic-Lectionary/master/"
              "MySQL/liturgy_lectionary_table_readings__list.sql")

# Calendar profiles. "general" is the General Roman Calendar as published (Epiphany 6 Jan,
# Ascension and Corpus Christi on Thursday). "general_sunday" is the same calendar with the
# three transfers most English-speaking conferences use. "us" adds the US national proper.
PROFILES = {
    "general": "calendar/{year}?year_type=CIVIL",
    "general_sunday": "calendar/{year}?year_type=CIVIL&epiphany=SUNDAY_JAN2_JAN8"
                      "&ascension=SUNDAY&corpus_christi=SUNDAY",
    "us": "calendar/nation/US/{year}?year_type=CIVIL",
}

TAMIL_BOOKS = {
    "திபா": "Ps", "மத்": "Mt", "யோவா": "Jn", "லூக்": "Lk", "மாற்": "Mk", "எசா": "Is",
    "திப": "Acts", "1கொரி": "1 Cor", "2கொரி": "2 Cor", "உரோ": "Rom", "எபி": "Heb",
    "எபே": "Eph", "தொநூ": "Gn", "1யோவா": "1 Jn", "2யோவா": "2 Jn", "3யோவா": "3 Jn",
    "திவெ": "Rv", "எசே": "Ez", "1பேது": "1 Pt", "2பேது": "2 Pt", "பிலி": "Phil",
    "1திமொ": "1 Tm", "2திமொ": "2 Tm", "எரே": "Jer", "சீஞா": "Sir", "விப": "Ex",
    "யாக்": "Jas", "கலா": "Gal", "1அர": "1 Kgs", "2அர": "2 Kgs", "இச": "Dt", "இணை": "Dt",
    "கொலோ": "Col", "1தெச": "1 Thes", "2தெச": "2 Thes", "1சாமு": "1 Sm", "2சாமு": "2 Sm",
    "சாஞா": "Wis", "ஓசே": "Hos", "தானி": "Dn", "நீமொ": "Prv", "தோபி": "Tb", "ஆமோ": "Am",
    "மீக்": "Mi", "எண்": "Nm", "செக்": "Zec", "யோவே": "Jl", "யோபு": "Jb", "1மக்": "1 Mc",
    "2மக்": "2 Mc", "தீத்": "Ti", "1குறி": "1 Chr", "2குறி": "2 Chr", "லேவி": "Lv",
    "செப்": "Zep", "யோனா": "Jon", "இபா": "Sg", "நீத": "Jgs", "மலா": "Mal", "யோசு": "Jos",
    "யூதி": "Jdt", "பாரூ": "Bar", "சஉ": "Eccl", "நெகே": "Neh", "எஸ்ரா": "Ezra",
    "புல": "Lam", "அப": "Hb", "ரூத்": "Ru", "பில": "Phlm", "ஆகா": "Hg", "யூதா": "Jude",
    "நாகூ": "Na", "எஸ்(கி)": "Est",
}
# The Tamil Bible prints Daniel's Greek additions as a separate book: (இ)1 = the Prayer of
# Azariah and Song of the Three (Dn 3:24-90, so verse + 23), (இ)2 = Susanna (Dn 13),
# (இ)3 = Bel and the Dragon (Dn 14).
DANIEL_ADDITIONS = {"1": ("3", 23), "2": ("13", 0), "3": ("14", 0)}

KIND_BY_TYPE = {"0": "PROCESSION_GOSPEL", "1": "FIRST_READING", "2": "PSALM",
                "3": "SECOND_READING", "4": "SEQUENCE", "5": "ACCLAMATION", "6": "GOSPEL"}
KIND_BY_COMMON_TYPE = {"1": "FIRST_READING", "2": "FIRST_READING_EASTER", "3": "PSALM",
                       "4": "SECOND_READING", "5": "ACCLAMATION", "6": "GOSPEL"}
SEQUENCES = {"Victimae": "Victimae paschali laudes", "VeniSancteSpiritus": "Veni, Sancte Spiritus",
             "LaudaSion": "Lauda Sion", "StabatMater": "Stabat Mater"}
DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]


# Known errors in the LitCal data (present in both its v5 and dev APIs as of 2026-10-14).
# CDW decree of 3 June 2016 raised St Mary Magdalene to a feast; CDW decree of 26 January 2021
# renamed St Martha's memorial "Saints Martha, Mary and Lazarus".
GRADE_CORRECTIONS = {"StMaryMagdalene": 4}
# Proper readings missing from the Tamil list: Our Lady of Guadalupe is a feast in the USA with its own
# readings (Lectionary 690A; Zec 2:14-17 there = 2:10-13 in RSV numbering); elsewhere it is an optional memorial and these are offered, not imposed.
PROPER_OVERRIDES = {
    "OurLadyOfGuadalupe": {"cycles": {"*": [
        {"kind": "FIRST_READING", "options": [{"citation": "Zec 2:10-13"}, {"citation": "Rv 11:19a; 12:1-6a, 10ab"}]},
        {"kind": "PSALM", "options": [{"citation": "Jdt 13:18bcde, 19"}]},
        {"kind": "GOSPEL", "options": [{"citation": "Lk 1:26-38"}, {"citation": "Lk 1:39-47"}]},
    ]}, "commons": ["Mary"], "source_day": "USCCB Lectionary 690A"},
}
# Holy Thursday has two Masses; the evening Mass of the Lord's Supper is the day's principal one
# (as in the USCCB calendar); the Chrism Mass stays available as the second celebration.
FIRST_ON_DAY = ["HolyThurs"]
NAME_CORRECTIONS = {"StMartha": "Saints Martha, Mary and Lazarus",
                    # Upstream still says "Blessed": canonized 2015 and 2012.
                    "JuniperoSerra": "[ US ] Saint Junípero Serra, Priest",
                    "KateriTekakwitha": "[ US ] Saint Kateri Tekakwitha, Virgin"}


def fetch(url: str, dest: Path) -> bytes:
    if dest.exists() and dest.stat().st_size > 0:
        return dest.read_bytes()
    dest.parent.mkdir(parents=True, exist_ok=True)
    req = urllib.request.Request(url, headers={"Accept": "application/json", "Accept-Language": "en",
                                               "User-Agent": "Cathopedia-ordo-builder"})
    with urllib.request.urlopen(req, timeout=120) as r:
        data = r.read()
    dest.write_bytes(data)
    time.sleep(0.5)  # be gentle with a volunteer-run API
    return data


# --------------------------------------------------------------------------- lectionary

def convert_citation(raw: str) -> str | None:
    """Tamil-siglum citation -> English abbreviation citation, or None if not a citation."""
    raw = raw.strip().replace("\\r", "").replace("\\n", "").strip()
    cf = raw.endswith("காண்க")  # Tamil "see", printed after an acclamation verse that is adapted
    raw = raw.removesuffix("காண்க").strip()
    # A reference can switch book after a semicolon ("யோவா1:7;லூக்1:17"): convert each part.
    parts = re.split(r";(?=\s*\d?[^\d\s:;,.-])", raw)
    if len(parts) > 1:
        converted = [convert_citation(part) for part in parts]
        if all(converted):
            joined = "; ".join(converted)
            return f"Cf. {joined}" if cf else joined
        return None
    single = _convert_single(raw)
    return f"Cf. {single}" if (single and cf) else single


def _convert_single(raw: str) -> str | None:
    m = re.match(r"^(\d?[^\d:(]+(?:\([^)]*\))?):?(\d.*)$", raw)
    if not m:
        return None
    book, rest = m.group(1), m.group(2)
    rest = rest.split("~")[0]  # "~EW…" points at another day's text, not part of the reference
    greek_daniel = book == "தானி(இ)"
    if greek_daniel:
        chap, _, verses = rest.partition(":")
        target, offset = DANIEL_ADDITIONS[chap]
        def shift(v: re.Match) -> str:
            return str(int(v.group(0)) + offset)
        verses = re.sub(r"(?<![\d])\d+", shift, verses) if offset else verses
        rest, book = f"{target}:{verses}", "தானி"
    if book not in TAMIL_BOOKS:
        return None
    if book == "தானி" and not greek_daniel and rest.startswith("3:"):
        # Catholic Bibles place the Greek prayer at Dn 3:24-90, so canonical 3:24-30 is 3:91-97.
        rest = "3:" + re.sub(r"\d+", lambda v: str(int(v.group(0)) + 67) if int(v.group(0)) >= 24 else v.group(0),
                            rest[2:])
    # Tamil lectionaries separate stanzas with "." and verses with ","; English uses ", ".
    rest = re.sub(r"\s*[.,]\s*", ", ", rest).replace(";", "; ")
    rest = re.sub(r"\s+", " ", rest).strip(" ,")
    return f"{TAMIL_BOOKS[book]} {rest}"


def load_tamil(sql_path: Path) -> "OrderedDict[str, list[tuple[str, str]]]":
    s = sql_path.read_text(encoding="utf-8")
    rows = re.findall(r"\('((?:[^'\\]|\\.)*)', ([\d.]+), '((?:[^'\\]|\\.)*)'\)", s)
    ids: OrderedDict[str, list[tuple[str, str]]] = OrderedDict()
    for day_id, typ, ref in rows:
        ids.setdefault(day_id.strip(), []).append((typ, ref))
    return ids


def build_readings(rows: list[tuple[str, str]], common: bool = False) -> list[dict]:
    """[(type, ref)] -> [{kind, options:[{citation, short?}], ...}] in Mass order."""
    out: OrderedDict[str, dict] = OrderedDict()
    for typ, ref in sorted(rows, key=lambda r: float(r[0])):
        whole, _, frac = typ.partition(".")
        if whole == "9":
            continue  # commons pointer, handled by caller
        kind = (KIND_BY_COMMON_TYPE if common else KIND_BY_TYPE).get(whole)
        if not kind:
            continue
        reading = out.setdefault(kind, {"kind": kind, "options": []})
        if re.match(r"^\d\.\d\d[19]", typ):
            reading["proper"] = True  # replaces the weekday's reading even on a memorial
        if kind == "SEQUENCE":
            reading["options"].append({"citation": SEQUENCES.get(ref.strip(), ref.strip())})
            continue
        for part in re.split(r"\s+or\s+|\|", ref):
            cit = convert_citation(part)
            if not cit:
                continue
            group = frac[:2] if common else frac[:1]
            option = {"citation": cit}
            siblings = [o for o in reading["options"] if o.get("_g") == group]
            if group and siblings:
                option["short"] = True
            option["_g"] = group
            reading["options"].append(option)
    readings = []
    for r in out.values():
        for o in r["options"]:
            o.pop("_g", None)
        if r["options"]:
            readings.append(r)
    return readings


def merge(base: list[dict], override: list[dict]) -> list[dict]:
    by_kind = OrderedDict((r["kind"], r) for r in base)
    for r in override:
        by_kind[r["kind"]] = r
    order = ["PROCESSION_GOSPEL", "FIRST_READING", "FIRST_READING_EASTER", "PSALM", "SECOND_READING",
             "SEQUENCE", "ACCLAMATION", "GOSPEL"]
    return sorted(by_kind.values(), key=lambda r: order.index(r["kind"]))


def commons_pointer(rows: list[tuple[str, str]]) -> list[str]:
    for typ, ref in rows:
        if typ.split(".")[0] == "9":
            return [p.split("~")[0].strip().lstrip("_") for p in re.split(r"\s+or\s+", ref.strip()) if p.strip()]
    return []


def temporale_ids(key: str) -> tuple[str | None, str]:
    """LitCal event key -> (Tamil base day id, cycle kind: 'sun' | 'wk' | 'none')."""
    m = re.fullmatch(r"OrdSunday(\d+)", key)
    if m:
        return f"OW{int(m.group(1)):02d}-0Sun", "sun"
    m = re.fullmatch(r"OrdWeekday(\d+)(\w+day)", key)
    if m:
        return f"OW{int(m.group(1)):02d}-{DAYS.index(m.group(2)) + 1}{m.group(2)[:3]}", "wk"
    m = re.fullmatch(r"(Advent|Lent|Easter)(\d)", key)
    if m:
        return f"{m.group(1)[0]}W0{m.group(2)}-0Sun", "sun"
    m = re.fullmatch(r"(Advent|Lent|Easter)Weekday(\d)(\w+day)", key)
    if m:
        return f"{m.group(1)[0]}W0{m.group(2)}-{DAYS.index(m.group(3)) + 1}{m.group(3)[:3]}", "none"
    m = re.fullmatch(r"AdventWeekdayDec(\d+)", key)
    if m:
        return f"AW05-Dec{int(m.group(1))}", "none"
    m = re.fullmatch(r"ChristmasWeekday(Dec|Jan)(\d+)", key)
    if m:
        return (f"CW01-Dec{m.group(2)}" if m.group(1) == "Dec" else f"CW02-Jan{int(m.group(2))}"), "none"
    m = re.fullmatch(r"DayAfterEpiphanyJan(\d+)", key)
    if m:
        return f"CW03-Day{int(m.group(1)) - 6}", "none"
    m = re.fullmatch(r"DayAfterEpiphany(\w+day)", key)
    if m:
        return f"CW03-Day{DAYS.index(m.group(1)) + 1}", "none"
    octave = {"Mon": 1, "Tue": 2, "Wed": 3, "Thu": 4, "Fri": 5, "Sat": 6}
    m = re.fullmatch(r"(Mon|Tue|Wed|Thu|Fri|Sat)OctaveEaster", key)
    if m:
        d = octave[m.group(1)]
        return f"EW01-{d}{DAYS[d - 1][:3]}", "none"
    m = re.fullmatch(r"(Mon|Tue|Wed)HolyWeek", key)
    if m:
        d = octave[m.group(1)]
        return f"LW06-{d}{DAYS[d - 1][:3]}", "none"
    fixed = {
        "ChristKing": ("OW34-0Sun", "sun"), "Christmas2": ("CW02-0Sun", "none"),
        "HolyFamily": ("CW01-HolyFamily", "sun"), "MaryMotherOfGod": ("Blessed Virgin Mary, the Mother of God", "none"),
        "Epiphany": ("CW03-Epiphany", "none"), "BaptismLord": ("CW04-Baptism", "sun"),
        "AshWednesday": ("LW00-3Wed", "none"), "ThursdayAfterAshWednesday": ("LW00-4Thu", "none"),
        "FridayAfterAshWednesday": ("LW00-5Fri", "none"), "SaturdayAfterAshWednesday": ("LW00-6Sat", "none"),
        "PalmSun": ("LW06-0Sun", "sun"), "HolyThursChrism": ("LW06-4Thu~Chrism", "none"),
        "HolyThurs": ("LW06-4Thu", "none"), "GoodFri": ("LW06-5Fri", "none"),
        "EasterVigil": ("LW06-6Sat", "sun"), "Easter": ("EW01-0Sun", "none"),
        "Ascension": ("EW07-Ascension", "sun"), "Pentecost": ("EW08-Pentecost", "sun"),
        "Pentecost_vigil": ("EW08-Pentecost - Vigil", "none"), "Trinity": ("OW00-Trinity", "sun"),
        "CorpusChristi": ("OW00-CorpusChristi", "sun"), "SacredHeart": ("OW00-SacredHeart", "sun"),
        "ImmaculateHeart": ("OW00-ImmaculateHeart", "none"), "MaryMotherChurch": ("OW00-MaryMotherofChurch", "none"),
    }
    return fixed.get(key, (None, "none"))


def cycle_entries(tamil, base: str, kind: str) -> dict[str, list[dict]] | None:
    """Readings per cycle ('A','B','C' / 'I','II' / '*') from a Tamil base id and its variants."""
    base_rows = tamil.get(base, [])
    variants = {"sun": ["A", "B", "C"], "wk": ["1", "2"]}.get(kind, [])
    out: dict[str, list[dict]] = {}
    shared = build_readings(base_rows)
    if variants:
        for v in variants:
            rows = tamil.get(f"{base} {v}")
            if rows is None and kind == "sun":
                rows = tamil.get(f"{base}{v}")  # one source row omits the space (OW11-0SunB)
            if rows is not None or shared:
                out[{"1": "I", "2": "II"}.get(v, v)] = merge(shared, build_readings(rows or []))
    elif shared:
        out["*"] = shared
    # Single-cycle overrides on otherwise shared days (e.g. "LW05-1Mon C", "AW01-1Mon A").
    if kind == "none":
        for v in "ABC":
            rows = tamil.get(f"{base} {v}")
            if rows:
                out[v] = merge(shared, build_readings(rows))
    return out or None


def norm_name(name: str) -> str:
    n = unicodedata.normalize("NFKD", name).encode("ascii", "ignore").decode().lower()
    n = re.sub(r"\(.*?\)", " ", n)
    n = re.sub(r",.*$", "", n)  # drop ", bishop and martyr" style titles
    n = re.sub(r"\b(saints?|sts?|blessed|bl|the|of|and|our|lady|pope|st)\b", " ", n)
    return re.sub(r"[^a-z]+", " ", n).strip()


SANCTORAL_ALIASES = {
    # LitCal key -> Tamil day id where the names differ too much to match automatically.
    "Christmas_vigil": "Nativity of the Lord 1", "Christmas": "Nativity of the Lord 4",
    "StJoseph": "Saint Joseph Husband of the Blessed Virgin Mary",
    "NativityJohnBaptist_vigil": "Birth of Saint John the Baptist - Vigil",
    "Assumption_vigil": "Assumption of the Blessed Virgin Mary - Vigil",
    "StsPeterPaulAp_vigil": "Saints Peter and Paul, Apostles - Vigil",
    "DedicationLateran": "Dedication of the Lateran basilica",
    "DedicationStMaryMajor": "Dedication of the Basilica of Saint Mary Major",
    "DedicationStsPeterPaul": "Dedication of the basilicas of Saints Peter and Paul, Apostles",
    "MotherGod": "Blessed Virgin Mary, the Mother of God",
    "AllSouls": "All Souls",
    "HolyInnocents": "Holy Innocents, martyrs",
    "StStephenProto": "Saint Stephen, the first martyr",
    "StJohnEvangelist": "Saint John the Apostle and evangelist",
    "BeheadingJohnBaptist": "The Beheading of Saint John the Baptist, martyr",
    "ConversionStPaul": "The Conversion of Saint Paul, apostle",
    "MostHolyNameJesus": "The Most Holy Name of Jesus",
    "Annunciation": "Annunciation of the Lord",
    "Presentation": "Presentation of the Lord",
    "PresentationMary": "Presentation of the Blessed Virgin Mary",
    "Visitation": "Visitation of the Blessed Virgin Mary",
    "QueenshipMary": "Queenship of Blessed Virgin Mary",
    "NativityMary": "Birth of the Blessed Virgin Mary",
    "ExaltationCross": "Exaltation of the Holy Cross",
    "Transfiguration": "Transfiguration of the Lord",
    "GuardianAngels": "Guardian Angels",
    "StsArchangels": "Saints Michael, Gabriel and Raphael, Archangels",
    "FirstMartyrsRome": "First Martyrs of the Church of Rome",
    "ChairStPeter": "Chair of Saint Peter, apostle",
    "NativityJohnBaptist": "Birth of Saint John the Baptist",
    "NativityVirginMary": "Birth of the Blessed Virgin Mary",
    "OurLadyOfLoreto": "Our Lady of Loreto",
    "JuanDiego": "Saint Juan Diego",
    "StAndrewAp": "Saint Andrew the Apostle",
    "StAndrewDungLac": "Saint Andrew Dung-Lac and his companions, martyrs",
    "StAnselm": "Saint Anselm of Canterbury, bishop and doctor of the Church",
    "StAnthonyEgypt": "Saint Anthony of Egypt, abbot",
    "StAugustineHippo": "Saint Augustine of Hippo, bishop and doctor of the Church",
    "StAugustineZhaoRong": "Saint Augustine Zhao Rong and companions, martyrs",
    "StBartholomewAp": "Saint Bartholomew the Apostle",
    "StBernardClairvaux": "Saint Bernard of Clairvaux, abbot and doctor of the Church",
    "StBridget": "Saint Birgitta, religious",
    "StChristopherMagallanes": "Saint Christopher Magallanes and companions, martyrs",
    "StDenis": "Saint Denis and companions, martyrs",
    "StFaustinaKowalska": "Saint Maria Faustina Kowalska, virgin",
    "StHilaryPoitiers": "Saint Hilary of Poitiers, bishop and doctor",
    "StJeanVianney": "Saint Jean Vianney (the Curé of Ars), priest",
    "StLukeEvangelist": "Saint Luke the Evangelist",
    "StMatthewEvangelist": "Saint Matthew the Evangelist, Apostle, Evangelist",
    "StsJeanBrebeuf": "Saints Jean de Brébeuf, Isaac Jogues, priests and martyrs; and their companions, martyrs",
}

# LitCal "common" labels -> Tamil commons, for celebrations with no proper readings in the list.
COMMON_BY_LABEL = {"Martyrs": "Martyr", "Pastors": "Pastor", "Doctors": "Doctor", "Virgins": "Virgin",
                   "Blessed Virgin Mary": "Mary", "Dedication of a Church": "Church",
                   "Holy Men and Women": "Saint"}


def easter(y: int) -> date:
    a, b, c = y % 19, y // 100, y % 100
    d, e = b // 4, b % 4
    f = (b + 8) // 25
    g = (b - f + 1) // 3
    h = (19 * a + b - d - g + 15) % 30
    i, k = c // 4, c % 4
    l = (32 + 2 * e + 2 * i - h - k) % 7
    m = (a + 11 * h + 22 * l) // 451
    month = (h + l - 7 * m + 114) // 31
    return date(y, month, (h + l - 7 * m + 114) % 31 + 1)


def ferial_key(d: date, sunday_epiphany: bool) -> str | None:
    """The weekday ("ferial") Mass of a non-Sunday, whose readings are used on memorials too.

    LitCal omits the weekday entry when an obligatory memorial or higher falls on it, but the
    Lectionary still uses the weekday readings on memorials, so the app needs this key every day.
    """
    if d.weekday() == 6:
        return None
    day = DAYS[d.weekday()]
    y = d.year
    dec24 = date(y, 12, 24)
    advent1 = dec24 - timedelta(days=(dec24.weekday() + 1) % 7) - timedelta(days=21)
    if d >= advent1:
        if d.month == 12 and d.day >= 25:
            return f"ChristmasWeekdayDec{d.day}" if d.day >= 29 else None
        if d.month == 12 and d.day >= 17:
            return f"AdventWeekdayDec{d.day}"
        return f"AdventWeekday{(d - advent1).days // 7 + 1}{day}"
    jan2 = date(y, 1, 2)
    epiphany = jan2 + timedelta(days=(6 - jan2.weekday()) % 7) if sunday_epiphany else date(y, 1, 6)
    if sunday_epiphany and epiphany.day >= 7:
        baptism = epiphany + timedelta(days=1)
    else:
        baptism = epiphany + timedelta(days=(6 - epiphany.weekday()) % 7 or 7)
    if d <= baptism:
        if d.day == 1 or d == epiphany or d == baptism:
            return None
        if d < epiphany:
            return f"ChristmasWeekdayJan{d.day}"
        return f"DayAfterEpiphany{day}" if sunday_epiphany else f"DayAfterEpiphanyJan{d.day}"
    e = easter(y)
    ash = e - timedelta(days=46)
    if d < ash:
        return f"OrdWeekday{(d - baptism).days // 7 + 1}{day}"
    if d < ash + timedelta(days=4):
        return ["AshWednesday", "ThursdayAfterAshWednesday", "FridayAfterAshWednesday",
                "SaturdayAfterAshWednesday"][(d - ash).days]
    if d < e - timedelta(days=7):
        return f"LentWeekday{(d - (ash + timedelta(days=4))).days // 7 + 1}{day}"
    if d < e:
        return {0: "MonHolyWeek", 1: "TueHolyWeek", 2: "WedHolyWeek", 3: "HolyThurs", 4: "GoodFri"}.get(d.weekday())
    if d < e + timedelta(days=7):
        return f"{day[:3]}OctaveEaster"
    pentecost = e + timedelta(days=49)
    if d < pentecost:
        return f"EasterWeekday{(d - e).days // 7 + 1}{day}"
    christ_king = advent1 - timedelta(days=7)
    sunday = d - timedelta(days=d.weekday() + 1)
    return f"OrdWeekday{34 - (christ_king - sunday).days // 7}{day}"


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--first", type=int, default=2025)
    ap.add_argument("--last", type=int, default=2040)
    args = ap.parse_args()
    years = range(args.first, args.last + 1)

    tamil = load_tamil_from_cache()
    # ---- calendars
    calendars: dict[str, dict[int, list[dict]]] = {}
    names: dict[str, str] = {}
    commons_of: dict[str, list[str]] = {}
    auto_matches: list[tuple[str, str, str]] = []
    for profile, path in PROFILES.items():
        calendars[profile] = {}
        for y in years:
            raw = fetch(f"{API}/{path.format(year=y)}", CACHE / f"{profile}_{y}.json")
            events = json.loads(raw)["litcal"]
            for e in events:
                e["grade"] = GRADE_CORRECTIONS.get(e["event_key"], e["grade"])
                e["name"] = NAME_CORRECTIONS.get(e["event_key"], e["name"])
            calendars[profile][y] = events
            for e in events:
                names.setdefault(e["event_key"], e["name"])
                if e.get("common"):
                    commons_of.setdefault(e["event_key"], e["common"])

    # ---- lectionary keyed by LitCal event key
    lectionary: dict[str, dict] = {}
    unmatched: list[str] = []
    tamil_names = {k: norm_name(k) for k in tamil if not re.match(r"^(OW|AW|CW|LW|EW)\d\d-|^_", k)}
    ferial_keys = {ferial_key(date(y, 1, 1) + timedelta(days=i), sun)
                   for y in years for i in range(366) for sun in (False, True)
                   if (date(y, 1, 1) + timedelta(days=i)).year == y}
    ferial_keys.discard(None)
    all_keys = sorted(set(names) | ferial_keys)
    for key in all_keys:
        base_key = key.removesuffix("_vigil")
        base, kind = temporale_ids(key)
        if base is None and key.endswith("_vigil") and key not in SANCTORAL_ALIASES:
            continue  # Saturday-evening vigil Masses take the Sunday's readings
        if base is None:
            base, kind = temporale_ids(base_key)
        if base is not None:
            entry = cycle_entries(tamil, base, kind)
            if entry:
                replaces = sorted({r["kind"] for c in entry.values() for r in c if r.pop("proper", False)})
                lectionary[key] = {"cycles": entry}
                if replaces:
                    lectionary[key]["replaces"] = replaces + (["ACCLAMATION"] if "GOSPEL" in replaces else [])
            else:
                unmatched.append(key)
            continue
        if re.match(r"SatMemBVM", key):
            continue  # weekday readings (or the Common of the BVM) are used
        tid = SANCTORAL_ALIASES.get(key)
        if tid is None:
            if key not in names:
                unmatched.append(key)
                continue
            target = norm_name(names[key])
            best = difflib.get_close_matches(target, tamil_names.values(), n=1, cutoff=0.86)
            if best:
                tid = next(k for k, v in tamil_names.items() if v == best[0])
                auto_matches.append((key, names[key], tid))
        if tid is None or tid not in tamil:
            commons = sorted({COMMON_BY_LABEL[c.split(":")[0]] for c in commons_of.get(key, [])
                              if c.split(":")[0] in COMMON_BY_LABEL})
            if commons:
                lectionary[key] = {"cycles": {}, "commons": commons}
            else:
                unmatched.append(key)
            continue
        rows = tamil[tid]
        entry = {"cycles": {"*": build_readings(rows)}}
        for v in "ABC":  # e.g. All Souls / Transfiguration gospels by Sunday cycle
            if f"{tid} {v}" in tamil:
                entry["cycles"][v] = merge(entry["cycles"]["*"], build_readings(tamil[f"{tid} {v}"]))
        if not entry["cycles"]["*"]:
            del entry["cycles"]["*"]
        commons = commons_pointer(rows)
        if commons:
            entry["commons"] = commons
        replaces = sorted({r["kind"] for c in entry["cycles"].values() for r in c if r.pop("proper", False)})
        if "GOSPEL" in replaces:
            replaces.append("ACCLAMATION")  # the acclamation follows its Gospel
        if replaces:
            entry["replaces"] = replaces
        if entry["cycles"] or commons:
            entry["source_day"] = tid
            lectionary[key] = entry
        else:
            unmatched.append(key)

    lectionary.update(PROPER_OVERRIDES)

    commons_out = {}
    for cid in [k for k in tamil if k.startswith("_")]:
        commons_out[cid.lstrip("_")] = build_readings(tamil[cid], common=True)

    # ---- compact per-profile calendars
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    for profile, by_year in calendars.items():
        out_years = {}
        for y, events in by_year.items():
            days: dict[str, dict] = {}
            sunday_epiphany = profile != "general"  # LitCal's US calendar also moves Epiphany to Sunday
            for e in events:
                if e.get("is_vigil_mass"):
                    continue  # the Mass of the following day; the app offers it from that day
                day = e["date"][5:10].replace("-", "")
                entry = days.setdefault(day, {"e": []})
                entry["e"].append([e["event_key"], e["grade"], "/".join(e.get("color") or []),
                                   e.get("liturgical_season") or "", e.get("psalter_week") or 0])
            for day, entry in days.items():
                entry["e"].sort(key=lambda ev: ev[0] not in FIRST_ON_DAY)  # stable: other events keep their order
                f = ferial_key(date(y, int(day[:2]), int(day[2:])), sunday_epiphany)
                if f:
                    entry["f"] = f
            out_years[str(y)] = days
        doc = {
            "profile": profile,
            "source": "Liturgical Calendar API (Apache-2.0), github.com/Liturgical-Calendar/LiturgicalCalendarAPI",
            "years": out_years,
        }
        (OUT_DIR / f"calendar_{profile}.json").write_text(json.dumps(doc, ensure_ascii=False, separators=(",", ":")))

    (OUT_DIR / "lectionary.json").write_text(json.dumps({
        "source": "Reading references: Tamil Catholic Lectionary reading list (public domain), "
                  "github.com/jayarathina/Tamil-Catholic-Lectionary; citations only, converted to English sigla.",
        "psalm_numbering": "RSV/KJV verse numbers, psalm titles not counted (NABRE and Grail often run one higher)",
        "events": lectionary,
        "commons": commons_out,
    }, ensure_ascii=False, separators=(",", ":")))
    (OUT_DIR / "names_en.json").write_text(json.dumps(names, ensure_ascii=False, separators=(",", ":")))

    print(f"events: {len(all_keys)}, with readings: {len(lectionary)}, unmatched: {len(unmatched)}")
    print("unmatched:", ", ".join(unmatched))
    (CACHE / "auto_matches.tsv").write_text("\n".join("\t".join(m) for m in auto_matches))


def load_tamil_from_cache():
    sql = CACHE / "tamil_readings_list.sql"
    fetch(TAMIL_LIST, sql)
    return load_tamil(sql)


if __name__ == "__main__":
    main()
