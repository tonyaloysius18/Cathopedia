#!/usr/bin/env python3
"""Map Cathopedia miracle ids -> eucharisticmiracles.faith slugs."""
import json, re, unicodedata, os, sys

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = "/Users/tonyaloysius/Documents/Cathopedia"

# Their slugs carry ISO-ish country prefixes and trailing years; neither helps matching.
PREFIX = re.compile(r"^(arg|aut|bel|col|cro|egy|fra|ger|hol|hon|ind|ita|mex|pol|por|spa|usa|ven)-")
STOP = {"the","of","and","saint","st","blessed","miracle","eucharistic","miracles",
        "communion","apparitions","sustenance","fasting","vision","life"}

def toks(s):
    s = unicodedata.normalize("NFKD", s).encode("ascii", "ignore").decode().lower()
    return {w for w in re.findall(r"[a-z]{3,}", s) if w not in STOP}

# Cases where no token overlap exists, or where two candidates tie.
OVERRIDES = {
    "balasar": "alexandrina-maria-da-costa-eucharistic-sustenance",
    "chateauneuf-de-galaure": "marthe-robin-eucharistic-sustenance",
    "dulmen": "anne-catherine-emmerich-eucharistic-sustenance",
    "konnersreuth": "teresa-neumann-eucharistic-sustenance",
    "bois-dhaine": "anne-louise-lateau-eucharistic-fasting",
    "sachseln": "st-nicholas-of-flue-eucharistic-fasting",
    "muro-lucano": "saint-gerard-majella-childhood",
    "saint-gilles": "saint-egidio-charles-martel-absolution",
    "naples-five-wounds": "saint-maria-francesca-five-wounds",
    "san-giorgio-a-cremano": "blessed-mary-passion-eucharistic-life",
    "vercelli-bicchieri": "blessed-emilia-bicchieri-communion",
    "orvieto-thomas-of-cori": "blessed-thomas-cori-apparitions",
    "bologna-imelda": "blessed-imelda-lambertini-bologna",
    "montieri": "blessed-james-montieri-jesus-communion",
    "krakow-divine-mercy": "divine-mercy-apparitions-cracow",
    "vienna-stanislaus-kostka": "saint-stanislaus-kostka-communion",
    "rome-devoted-lambs": "devoted-lambs-eucharist",
    "rome-gregory-great": "ita-rome-6th-7th",
    "lateran-council": "ita-rome-1215",
    "guadalupe-mexico": "our-lady-of-guadalupe-1531",
    "guadalupe-spain": "spa-guadalupe",
    "lourdes-healings": "lourdes-eucharistic-procession-1888",
    "fatima-angel": "fatima-angel-of-peace-1916",
    "calanda": "calanda-miracle-1640",
    "carmelite-siena": "siena-carmelite-monk-miracle",
    "miracle-of-siena": "ita-siena",
    "eucharistic-miracle-buenos-aires": "arg-buenos-aires",
    "san-juan-honduras": "hon-san-juan-2022",
    "san-juan-de-las-abadesas": "saint-john-of-the-abbesses-1251",
    "valencia-santo-caliz": "spa-valencia",
    "mary-of-egypt": "egy-st-mary-egypt",
    "peter-damian": "ita-s-peter-damian",
    "assisi-clare": "ita-saint-clare-assisi",
    "montefalco-clare": "montefalco-saint-clare-communion",
    "foligno-angela": "foligno-blessed-angela-vision",
    "proceno-agnes": "proceno-saint-agnes-segni-communion",
    "chiaravalle-bernard": "saint-bernard-chiaravalle-duke-conversion",
    "pitigliano": "saint-lucia-filippini-pitigliano",
    "saint-satyrus": "saint-satyrus-shipwreck",
    "pibrac": "pibrac-eucharistic-miracle",
    "meerssen": "meerssen-1222",
    "meerssen-fire-1465": "meerssen-1465-fire",
    "turin": "ita-turin-1453",
    "turin-1640": "ita-turin-1640",
    "kranenburg": "kranenburg-bei-kleve-1280",
    "regensburg": "regensburg-dropped-hosts-1255",
    "neuvy-saint-sepulcre": "neuvy-saint-sepulcre-precious-blood",
    "scete": "egy-scete",
    "gruaro": "gruaro-valvasone-1294",
    "ettiswil": "ettiswill-1447",
    "alcala-de-henares": "alcala-1597",
    "bergen-op-zoom": "bergen-netherlands-1421",
    "middleburg": "middleburg-lovanio-1374",
    "eten": "eten-peru-1649",
    "morne-rouge": "morne-rouge-1902",
    "saint-andre-de-la-reunion": "saint-andre-de-la-reunion-1902",
    "chirattakonam": "chirattakonam-2001",
    "vilakkannur": "ind-vilakkannur-2013",
    "miracle-of-bolsena": "ita-bolsena",
    "miracle-of-santarem": "por-santarem",
    "o-cebreiro": "spa-ocebreiro",
    "bois-seigneur-isaac": "bel-bois-seigneur-isaac",
    "seefeld": "aut-seefeld",
    "legnica": "pol-legnica",
    # Their site splits these across two pages; we merge both galleries.
    "kranenburg": ["ger-kranenburg", "kranenburg-bei-kleve-1280"],
    "meerssen": ["hol-meerssen", "meerssen-1222"],
}

# Entries of theirs we deliberately carry no gallery for (no Cathopedia entry yet).
NO_ENTRY = {"bel-brussels", "fra-paris"}

def main():
    ours = []
    for line in open(os.path.join(HERE, "our_miracles.txt")):
        i, loc, yr = [x.strip() for x in line.split("|")]
        ours.append((i, loc, yr))
    mf = os.path.join(HERE, "em", "manifest.json")
    if os.path.exists(mf):
        theirs = list(json.load(open(mf)).keys())
    else:  # crawl still running - fall back to the sitemap slug list
        theirs = [l.strip() for l in open(os.path.join(HERE, "em_slugs.txt")) if l.strip()]
    cand = {t: toks(PREFIX.sub("", re.sub(r"-\d{4}$", "", t))) for t in theirs}

    mapping, unresolved = {}, []
    for i, loc, yr in ours:
        if i in OVERRIDES:
            ts = OVERRIDES[i]
            ts = ts if isinstance(ts, list) else [ts]
            missing = [t for t in ts if t not in cand]
            if missing:
                unresolved.append((i, loc, f"OVERRIDE MISSING: {missing}")); continue
            mapping[i] = ts; continue
        want = toks(i) | toks(loc)
        scored = sorted(((len(want & c) + (1.5 if yr != "None" and yr in t else 0), t)
                         for t, c in cand.items()), reverse=True)
        if scored[0][0] >= 1 and scored[0][0] > scored[1][0]:
            mapping[i] = [scored[0][1]]
        else:
            unresolved.append((i, loc, scored[:3]))

    rev = {}
    for k, vs in mapping.items():
        for v in vs:
            rev.setdefault(v, []).append(k)
    dupes = {v: ks for v, ks in rev.items() if len(ks) > 1}
    used = {v for vs in mapping.values() for v in vs}
    orphans = sorted(set(cand) - used - NO_ENTRY)

    json.dump(mapping, open(os.path.join(HERE, "slug_map.json"), "w"), indent=2, sort_keys=True)
    print(f"mapped {len(mapping)}/{len(ours)}")
    print(f"\nUNRESOLVED ({len(unresolved)}):")
    for u in unresolved: print("  ", u)
    print(f"\nCOLLISIONS ({len(dupes)}):")
    for v, ks in dupes.items(): print("  ", v, "<-", ks)
    print(f"\nTHEIRS WITH NO ENTRY OF OURS ({len(orphans)}):")
    for o in orphans: print("  ", o)

main()
