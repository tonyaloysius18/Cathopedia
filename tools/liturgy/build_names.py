#!/usr/bin/env python3
"""Localized celebration names for the offline Ordo: files/content/ordo/names_<lang>.json.

Saints, Sundays and feasts come from the Liturgical Calendar API's source translations
(Apache-2.0; missals/*/i18n and decrees/i18n). Weekday names, which that project generates in
code, are built here from each language's usual liturgical phrasing. Tamil has no upstream
translation: seasons and the main feasts are written here and the saints in names_ta.py. Run after build_ordo.py.
"""
from __future__ import annotations

import json
import re
import urllib.request
from pathlib import Path

from names_ta import TA_SAINTS
from names_us import US_NAMES

ROOT = Path(__file__).resolve().parents[2]
CACHE = ROOT / "build" / "liturgy-cache" / "i18n"
ORDO = ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "content" / "ordo"
SRC = "https://raw.githubusercontent.com/Liturgical-Calendar/LiturgicalCalendarAPI/development/jsondata/sourcedata/rite/roman"
FILES = ["missals/propriumdetempore", "missals/propriumdesanctis_1970", "missals/propriumdesanctis_2002",
         "missals/propriumdesanctis_2008", "decrees"]
DAY_KEYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]


def roman(n: int) -> str:
    out = ""
    for v, s in [(10, "X"), (9, "IX"), (5, "V"), (4, "IV"), (1, "I")]:
        while n >= v:
            out += s
            n -= v
    return out


# For each language: weekday names, a "weekday of week n of season" pattern, season phrases, and
# date-based labels for the late-Advent and Christmas-season weekdays.
L = {
 "fr": dict(days=["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"],
            week=lambda d, n, s: f"{d} de la {'1re' if n == 1 else f'{n}e'} semaine {s}",
            seasons=dict(ord="du Temps ordinaire", adv="de l'Avent", lent="de Carême", east="de Pâques"),
            months=dict(dec="décembre", jan="janvier"),
            advdec=lambda d: f"Avent : {d} décembre", xmasdec=lambda d: f"{d} décembre, dans l'octave de Noël",
            xmasjan=lambda d: f"{d} janvier, temps de Noël", epjan=lambda d: f"{d} janvier, après l'Épiphanie",
            epday=lambda D: f"{D} après l'Épiphanie", ash=lambda D: f"{D} après les Cendres",
            satmary="Mémoire de la Vierge Marie le samedi"),
 "it": dict(days=["Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"],
            week=lambda d, n, s: f"{d} della {n}ª settimana {s}",
            seasons=dict(ord="del Tempo Ordinario", adv="di Avvento", lent="di Quaresima", east="di Pasqua"),
            advdec=lambda d: f"Avvento: {d} dicembre", xmasdec=lambda d: f"{d} dicembre, ottava di Natale",
            xmasjan=lambda d: f"{d} gennaio, tempo di Natale", epjan=lambda d: f"{d} gennaio, dopo l'Epifania",
            epday=lambda D: f"{D} dopo l'Epifania", ash=lambda D: f"{D} dopo le Ceneri",
            satmary="Memoria della Beata Vergine Maria in sabato"),
 "de": dict(days=["Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"],
            week=lambda d, n, s: f"{d} der {n}. Woche {s}",
            seasons=dict(ord="im Jahreskreis", adv="im Advent", lent="der Fastenzeit", east="der Osterzeit"),
            advdec=lambda d: f"Advent: {d}. Dezember", xmasdec=lambda d: f"{d}. Dezember, Weihnachtsoktav",
            xmasjan=lambda d: f"{d}. Januar, Weihnachtszeit", epjan=lambda d: f"{d}. Januar, nach Erscheinung des Herrn",
            epday=lambda D: f"{D} nach Erscheinung des Herrn", ash=lambda D: f"{D} nach Aschermittwoch",
            satmary="Gedenktag der seligen Jungfrau Maria am Samstag"),
 "es": dict(days=["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"],
            week=lambda d, n, s: f"{d} de la {n}.ª semana {s}",
            seasons=dict(ord="del Tiempo Ordinario", adv="de Adviento", lent="de Cuaresma", east="de Pascua"),
            advdec=lambda d: f"Adviento: {d} de diciembre", xmasdec=lambda d: f"{d} de diciembre, octava de Navidad",
            xmasjan=lambda d: f"{d} de enero, tiempo de Navidad", epjan=lambda d: f"{d} de enero, después de Epifanía",
            epday=lambda D: f"{D} después de Epifanía", ash=lambda D: f"{D} después de Ceniza",
            satmary="Memoria de Santa María en sábado"),
 "pt": dict(days=["Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"],
            week=lambda d, n, s: f"{d} da {n}ª semana {s}",
            seasons=dict(ord="do Tempo Comum", adv="do Advento", lent="da Quaresma", east="da Páscoa"),
            advdec=lambda d: f"Advento: {d} de dezembro", xmasdec=lambda d: f"{d} de dezembro, oitava do Natal",
            xmasjan=lambda d: f"{d} de janeiro, tempo do Natal", epjan=lambda d: f"{d} de janeiro, depois da Epifania",
            epday=lambda D: f"{D} depois da Epifania", ash=lambda D: f"{D} depois das Cinzas",
            satmary="Memória de Santa Maria no sábado"),
 "nl": dict(days=["Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag"],
            week=lambda d, n, s: f"{d} in de {n}e week {s}",
            seasons=dict(ord="door het jaar", adv="van de advent", lent="van de veertigdagentijd", east="van Pasen"),
            advdec=lambda d: f"Advent: {d} december", xmasdec=lambda d: f"{d} december, octaaf van Kerstmis",
            xmasjan=lambda d: f"{d} januari, kersttijd", epjan=lambda d: f"{d} januari, na Driekoningen",
            epday=lambda D: f"{D} na Driekoningen", ash=lambda D: f"{D} na Aswoensdag",
            satmary="Gedachtenis van Maria op zaterdag"),
 "pl": dict(days=["Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"],
            week=lambda d, n, s: f"{d} {roman(n)} tygodnia {s}",
            seasons=dict(ord="zwykłego", adv="Adwentu", lent="Wielkiego Postu", east="wielkanocnego"),
            advdec=lambda d: f"Adwent: {d} grudnia", xmasdec=lambda d: f"{d} grudnia, oktawa Narodzenia Pańskiego",
            xmasjan=lambda d: f"{d} stycznia, okres Narodzenia Pańskiego", epjan=lambda d: f"{d} stycznia, po Objawieniu Pańskim",
            epday=lambda D: f"{D} po Objawieniu Pańskim", ash=lambda D: f"{D} po Popielcu",
            satmary="Wspomnienie Najświętszej Maryi Panny w sobotę"),
 "ta": dict(days=["திங்கள்", "செவ்வாய்", "புதன்", "வியாழன்", "வெள்ளி", "சனி"],
            week=lambda d, n, s: f"{s} {n}ஆம் வாரம் – {d}",
            seasons=dict(ord="பொதுக்காலம்", adv="திருவருகைக் காலம்", lent="தவக்காலம்", east="பாஸ்கா காலம்"),
            advdec=lambda d: f"திருவருகைக் காலம் – டிசம்பர் {d}", xmasdec=lambda d: f"டிசம்பர் {d} – கிறிஸ்து பிறப்பு எண்கிழமை",
            xmasjan=lambda d: f"ஜனவரி {d} – கிறிஸ்து பிறப்புக் காலம்", epjan=lambda d: f"ஜனவரி {d} – திருக்காட்சிக்குப் பின்",
            epday=lambda D: f"திருக்காட்சிக்குப் பின் {D}", ash=lambda D: f"திருநீற்றுப் புதனுக்குப் பின் {D}",
            satmary="சனிக்கிழமை அன்னை மரியாவின் நினைவு"),
}

# Tamil has no upstream translation: Sundays by pattern, plus the main celebrations.
TA_SUNDAY = {"OrdSunday": "பொதுக்காலம்", "Advent": "திருவருகைக் காலம்", "Lent": "தவக்காலம்", "Easter": "பாஸ்கா காலம்"}
TA_FIXED = {
    "Christmas": "ஆண்டவரின் பிறப்புப் பெருவிழா", "Christmas2": "கிறிஸ்து பிறப்புக்குப் பின் இரண்டாம் ஞாயிறு",
    "HolyFamily": "திருக்குடும்பப் பெருவிழா", "MaryMotherOfGod": "இறைவனின் தாயாம் புனித கன்னி மரியா பெருவிழா",
    "Epiphany": "ஆண்டவரின் திருக்காட்சிப் பெருவிழா", "BaptismLord": "ஆண்டவரின் திருமுழுக்கு விழா",
    "AshWednesday": "திருநீற்றுப் புதன்", "PalmSun": "ஆண்டவரின் பாடுகளின் குருத்து ஞாயிறு",
    "MonHolyWeek": "புனித வாரத் திங்கள்", "TueHolyWeek": "புனித வாரச் செவ்வாய்", "WedHolyWeek": "புனித வாரப் புதன்",
    "HolyThursChrism": "புனித வியாழன் – திருத்தைலத் திருப்பலி", "HolyThurs": "ஆண்டவரின் இறுதி இராவுணவுத் திருப்பலி",
    "GoodFri": "ஆண்டவரின் திருப்பாடுகளின் வெள்ளி", "EasterVigil": "பாஸ்கா திருவிழிப்பு", "Easter": "ஆண்டவரின் உயிர்ப்புப் பெருவிழா",
    "MonOctaveEaster": "பாஸ்கா எண்கிழமைத் திங்கள்", "TueOctaveEaster": "பாஸ்கா எண்கிழமைச் செவ்வாய்",
    "WedOctaveEaster": "பாஸ்கா எண்கிழமைப் புதன்", "ThuOctaveEaster": "பாஸ்கா எண்கிழமை வியாழன்",
    "FriOctaveEaster": "பாஸ்கா எண்கிழமை வெள்ளி", "SatOctaveEaster": "பாஸ்கா எண்கிழமைச் சனி",
    "Ascension": "ஆண்டவரின் விண்ணேற்றப் பெருவிழா", "Pentecost": "தூய ஆவியார் பெருவிழா",
    "Trinity": "மூவொரு இறைவன் பெருவிழா", "CorpusChristi": "கிறிஸ்துவின் திருவுடல் திருஇரத்தப் பெருவிழா",
    "SacredHeart": "இயேசுவின் திருஇதயப் பெருவிழா", "ImmaculateHeart": "மரியாவின் மாசற்ற இதயம்",
    "MaryMotherChurch": "திருச்சபையின் தாய் புனித கன்னி மரியா", "ChristKing": "இயேசு கிறிஸ்து அனைத்துலக அரசர் பெருவிழா",
    "Annunciation": "ஆண்டவருக்கு மங்கள வார்த்தைப் பெருவிழா", "StJoseph": "புனித யோசேப்பு பெருவிழா",
    "NativityJohnBaptist": "திருமுழுக்கு யோவானின் பிறப்புப் பெருவிழா", "StsPeterPaulAp": "திருத்தூதர்கள் புனித பேதுரு, பவுல் பெருவிழா",
    "Assumption": "புனித கன்னி மரியாவின் விண்ணேற்புப் பெருவிழா", "AllSaints": "புனிதர் அனைவர் பெருவிழா",
    "AllSouls": "இறந்த விசுவாசிகள் அனைவரின் நினைவு", "ImmaculateConception": "புனித கன்னி மரியாவின் அமல உற்பவப் பெருவிழா",
    "Transfiguration": "ஆண்டவரின் உருமாற்றம்", "ExaltationCross": "திருச்சிலுவையின் மாட்சி விழா",
    "Presentation": "ஆண்டவரைக் காணிக்கையாக அர்ப்பணித்தல்", "DedicationLateran": "இலாத்தரன் பேராலய நேர்ந்தளிப்பு",
}

# Known slips in the upstream names, and gaps: upstream de and pt lack 20 February–March celebrations.
CORRECTIONS = {
    "fr": {"StTeresaJesus": "Sainte Thérèse d'Avila, vierge et docteur de l'Église"},
    "de": {
        "StScholastica": "Hl. Scholastika, Jungfrau",
        "OurLadyOfLourdes": "Gedenktag Unserer Lieben Frau in Lourdes",
        "StsCyrilMethodius": "Hll. Cyrill, Mönch, und Methodius, Bischof",
        "SevenHolyFounders": "Hll. Sieben Gründer des Servitenordens",
        "StPeterDamian": "Hl. Petrus Damiani, Bischof und Kirchenlehrer",
        "ChairStPeter": "Kathedra Petri",
        "StPolycarp": "Hl. Polykarp, Bischof und Märtyrer",
        "StCasimir": "Hl. Kasimir",
        "StsPerpetuaFelicity": "Hll. Perpetua und Felizitas, Märtyrinnen",
        "StJohnGod": "Hl. Johannes von Gott, Ordensgründer",
        "StFrancesRome": "Hl. Franziska von Rom, Ordensfrau",
        "StPatrick": "Hl. Patrick, Bischof",
        "StCyrilJerusalem": "Hl. Cyrill von Jerusalem, Bischof und Kirchenlehrer",
        "StJoseph": "Hl. Josef, Bräutigam der Gottesmutter Maria",
        "StTuribius": "Hl. Turibio von Mongrovejo, Bischof",
        "Annunciation": "Verkündigung des Herrn",
        "StFrancisPaola": "Hl. Franz von Paola, Einsiedler",
        "StIsidore": "Hl. Isidor, Bischof und Kirchenlehrer",
        "StVincentFerrer": "Hl. Vinzenz Ferrer, Priester",
        "StJohnBaptistDeLaSalle": "Hl. Johannes Baptist de La Salle, Priester",
    },
    "pt": {
        "StScholastica": "Santa Escolástica, virgem",
        "OurLadyOfLourdes": "Nossa Senhora de Lourdes",
        "StsCyrilMethodius": "São Cirilo, monge, e São Metódio, bispo",
        "SevenHolyFounders": "Santos Sete Fundadores da Ordem dos Servos de Maria",
        "StPeterDamian": "São Pedro Damião, bispo e doutor da Igreja",
        "ChairStPeter": "Cátedra de São Pedro, apóstolo",
        "StPolycarp": "São Policarpo, bispo e mártir",
        "StCasimir": "São Casimiro",
        "StsPerpetuaFelicity": "Santas Perpétua e Felicidade, mártires",
        "StJohnGod": "São João de Deus, religioso",
        "StFrancesRome": "Santa Francisca Romana, religiosa",
        "StPatrick": "São Patrício, bispo",
        "StCyrilJerusalem": "São Cirilo de Jerusalém, bispo e doutor da Igreja",
        "StJoseph": "São José, Esposo da Bem-aventurada Virgem Maria",
        "StTuribius": "São Turíbio de Mogrovejo, bispo",
        "Annunciation": "Anunciação do Senhor",
        "StFrancisPaola": "São Francisco de Paula, eremita",
        "StIsidore": "Santo Isidoro, bispo e doutor da Igreja",
        "StVincentFerrer": "São Vicente Ferrer, presbítero",
        "StJohnBaptistDeLaSalle": "São João Batista de la Salle, presbítero",
    },
}


def fetch(path: str, lang: str) -> dict:
    dest = CACHE / f"{path.replace('/', '_')}_{lang}.json"
    if not dest.exists():
        dest.parent.mkdir(parents=True, exist_ok=True)
        try:
            dest.write_bytes(urllib.request.urlopen(f"{SRC}/{path}/i18n/{lang}.json").read())
        except Exception:
            dest.write_text("{}")
    d = json.loads(dest.read_text())
    return {k: v for k, v in d.items() if isinstance(v, str)} if isinstance(d, dict) else {}


def generated(key: str, t: dict) -> str | None:
    days = dict(zip(DAY_KEYS, t["days"]))
    s = t["seasons"]
    for prefix, season in (("OrdWeekday", "ord"), ("AdventWeekday", "adv"), ("LentWeekday", "lent"), ("EasterWeekday", "east")):
        m = re.fullmatch(prefix + r"(\d+)(\w+day)", key)
        if m:
            return t["week"](days[m.group(2)], int(m.group(1)), s[season])
    if m := re.fullmatch(r"AdventWeekdayDec(\d+)", key):
        return t["advdec"](int(m.group(1)))
    if m := re.fullmatch(r"ChristmasWeekdayDec(\d+)", key):
        return t["xmasdec"](int(m.group(1)))
    if m := re.fullmatch(r"ChristmasWeekdayJan(\d+)", key):
        return t["xmasjan"](int(m.group(1)))
    if m := re.fullmatch(r"DayAfterEpiphanyJan(\d+)", key):
        return t["epjan"](int(m.group(1)))
    if m := re.fullmatch(r"DayAfterEpiphany(\w+day)", key):
        return t["epday"](days[m.group(1)])
    if m := re.fullmatch(r"(Thursday|Friday|Saturday)AfterAshWednesday", key):
        return t["ash"](days[m.group(1)])
    if key.startswith("SatMemBVM"):
        return t["satmary"]
    return None


def main() -> None:
    keys: set[str] = set()
    for profile in ("general", "general_sunday", "us"):
        for days in json.loads((ORDO / f"calendar_{profile}.json").read_text())["years"].values():
            for d in days.values():
                keys |= {e[0] for e in d["e"]}
                if d.get("f"):
                    keys.add(d["f"])
    for lang, t in L.items():
        names: dict[str, str] = {}
        if lang != "ta":
            for path in FILES:
                names.update(fetch(path, lang))
        else:
            names.update(TA_FIXED)
            names.update(TA_SAINTS)
            for k in keys:
                if m := re.fullmatch(r"(OrdSunday|Advent|Lent|Easter)(\d+)", k):
                    names[k] = f"{TA_SUNDAY[m.group(1)]} {m.group(2)}ஆம் ஞாயிறு"
        out = {}
        for k in sorted(keys):
            v = CORRECTIONS.get(lang, {}).get(k) or US_NAMES.get(lang, {}).get(k) or generated(k, t) or names.get(k)
            if v and lang == "pt":
                # Upstream pt keeps the Missal heading form ("Na Natividade do Senhor"); a calendar wants the name.
                v = re.sub(r"^N[ao]s? (\w)", lambda m: m.group(1).upper(), v).replace("Santo Paulo", "São Paulo")
            if v:
                out[k] = v
        (ORDO / f"names_{lang}.json").write_text(json.dumps(out, ensure_ascii=False, separators=(",", ":")))
        print(f"{lang}: {len(out)}/{len(keys)} names")


if __name__ == "__main__":
    main()
