#!/usr/bin/env python3
"""Parse each encyclical's subject line and opening out of the cached page text.

The Holy See prints:

    TITLE   ENCYCLICAL OF POPE X   <SUBJECT>   To the Venerable Brethren ...

The subject is the run of capitals between the pope's name and the addressee.
Anchoring on "ENCYCLICAL OF POPE" rather than on the title matters: many pages
head the document with a shortened title ("ARCANUM" for Arcanum Divinae), so a
title search lands in the site navigation instead.
"""
import json, re

CACHE = "/tmp/encyclical_texts.json"
# Leo XIV's layout puts the title between the two halves:
#   "ENCYCLICAL LETTER MAGNIFICA HUMANITAS OF HIS HOLINESS POPE LEO XIV ON ..."
# so the title is allowed to sit inside the anchor.
ANCHOR = re.compile(
    r"ENCYCLICAL\s+(?:LETTER\s+)?(?:[A-Z][A-Za-z']*\s+){0,4}?"
    r"(?:OF\s+(?:POPE\s+)?(?:HIS\s+HOLINESS\s+)?(?:POPE\s+)?)?", re.I)
# Some pages run the words together ("OFPOPE") or drop "POPE" entirely, so the
# lead-in is stripped here rather than relied on in the anchor.
POPE_NAME = re.compile(
    r"(?i)^\s*(of\s*pope|ofpope|of)?\s*"
    r"(leo|pius|benedict|john\s+paul|john|paul|francis)\s*[ivx]*\s*")
# Both the mixed-case and the all-caps forms of the address line.
# The words a subject line actually begins with, across both layouts.
SUBJECT_WORD = re.compile(
    r"\b(ON|CONCERNING|COMMENDING|PROCLAIMING|PLEADING|ANNOUNCING|URGING|"
    r"APPEALING|LAMENTING|RENEWING|DEPLORING|COMMEMORATING|RECOMMENDING|"
    r"IN\s+COMMEMORATION|FOR\s+THE|AGAINST|PROMULGATING|EXHORTING)\s+(?=[A-Z])")
# "TO THE" only ends the subject when an addressee follows it.
ADDRESS_RUNON = re.compile(
    # "TO THE THE PATRIARCHS" appears on at least one page, hence the repeat.
    r"(?i)\s+TO\s+(?:THE\s+|OUR\s+|ALL\s+|HIS\s+|THEIR\s+|EACH\s+)*"
    r"(?=VENERABLE|BISHOPS|PATRIARCHS|PRIMATES|ARCHBISHOPS|CARDINALS?|BELOVED|"
    r"EMINENT|EMINENCE|BROTHERS|BRETHREN|CLERGY|FAITHFUL|ORDINARIES|HIERARCHY|"
    r"PROFESSORS|STUDENTS|SONS|DAUGHTERS|PEOPLE\s+OF)")
# A section heading that runs straight on from the subject line.
SECTION_TAIL = re.compile(
    r"(?i)\s+(INTRODUCTION|PREFACE|BLESSING|CHAPTER\s+[A-Z]+|PROEMIUM|FOREWORD)\s*$")
# A trailing month is a date bleeding in ("... OF PEOPLES MARCH") unless a
# preposition makes it part of the subject ("... FOR PEACE DURING MAY").
TRAILING_MONTH = re.compile(
    r"(?i)(?<!\bDURING)(?<!\bIN)(?<!\bOF)(?<!\bFOR)\s+"
    r"(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|"
    r"NOVEMBER|DECEMBER)\s*$")
ADDRESSEE = re.compile(r"(?i)\bTO\s+(THE|OUR|ALL|HIS|THEIR|VENERABLE|BELOVED|EACH|JAMES|CARDINAL)\b")

# Pages that print no subject line in a form the parser can reach. Each value is
# taken from that document's own heading in the cached text, not from memory.
MANUAL = {
    "ut-unum-sint": "ON COMMITMENT TO ECUMENISM",
    "evangelium-vitae": "ON THE VALUE AND INVIOLABILITY OF HUMAN LIFE",
    "veritatis-splendor": "ON FUNDAMENTAL QUESTIONS OF THE CHURCH'S MORAL TEACHING",
    "centesimus-annus": "ON THE HUNDREDTH ANNIVERSARY OF RERUM NOVARUM",
    "redemptoris-missio": "ON THE PERMANENT VALIDITY OF THE CHURCH'S MISSIONARY MANDATE",
    "sollicitudo-rei-socialis": "ON THE TWENTIETH ANNIVERSARY OF POPULORUM PROGRESSIO",
    "redemptoris-mater": "ON THE BLESSED VIRGIN MARY IN THE LIFE OF THE PILGRIM CHURCH",
    "dominum-et-vivificantem": "ON THE HOLY SPIRIT IN THE LIFE OF THE CHURCH AND THE WORLD",
    "slavorum-apostoli": "IN COMMEMORATION OF SAINTS CYRIL AND METHODIUS",
    "laborem-exercens": "ON HUMAN WORK",
    "dives-in-misericordia": "ON THE MERCY OF GOD",
    "fidei-donum": "ON THE PRESENT CONDITION OF THE CATHOLIC MISSIONS, ESPECIALLY IN AFRICA",
    "auspicia-quaedam": "ON PUBLIC PRAYERS FOR WORLD PEACE AND SOLUTION OF THE PROBLEM OF PALESTINE",
    # Leo XIV's heading nests the title inside the formula, which the parser
    # cannot separate from the subject.
    "magnifica-humanitas": "ON SAFEGUARDING THE HUMAN PERSON IN THE TIME OF ARTIFICIAL INTELLIGENCE",
}


# vatican.va renders some subject lines with words run together across a line
# break, or with a stray marker left over from the page furniture. These cannot
# be repaired generically because the source is all caps, so they are listed.
CORRECTIONS = {
    "On Catholicismin the United States.": "On Catholicism in the United States.",
    "On the Latin American Bishops' Plenarycouncil.": "On the Latin American Bishops' Plenary Council.",
    "On Propagation of the Faith and Easternchurches.": "On Propagation of the Faith and Eastern Churches.",
    "On the Hundreth Anniversary of Rerum Novarum.": "On the Hundredth Anniversary of Rerum Novarum.",
}
# A lone letter or symbol left dangling at the end ("... INTELLIGENCE [", "... T").
TRAILING_JUNK = re.compile(r"\s+([A-Z]|\[|\*|\]|_+)$")


def strip_title_echo(subject, title):
    """Some pages repeat the document title at the end of the subject line."""
    t = re.escape(title.strip())
    return re.sub(rf"(?i)\s+{t}\s*$", "", subject).strip(" ,.;:")


def parse(raw):
    """Find the subject line, whichever side of the addressee it sits on.

    Two layouts are in use. The older one puts the subject before the address:
        ENCYCLICAL OF POPE LEO XIII  ON MARRIAGE LEGISLATION  To the Venerable...
    The modern one puts it after:
        LUMEN FIDEI OF THE SUPREME PONTIFF FRANCIS  TO THE BISHOPS ...  ON FAITH
    Anchoring on the subject's own opening word handles both, and the subject is
    always the all-caps run that follows it.
    """
    m = ANCHOR.search(raw)
    if not m:
        return None, None
    after = raw[m.end():]
    head = after[:1600]

    best = None
    for km in SUBJECT_WORD.finditer(head):
        taken = []
        for w in head[km.start():].split():
            core = re.sub(r"[^A-Za-z]", "", w)
            if core and not core.isupper():
                break
            if re.match(r"^\d", w):      # "1." starts the first numbered section
                break
            taken.append(w)
            if len(taken) > 22:
                break
        cand = re.sub(r"\s+", " ", " ".join(taken)).strip(" ,.;:")
        # Cut an address that ran on ("... TO THE BISHOPS OF ..."), but not a
        # subject that legitimately contains "to the" ("COMMENDING DEVOTION TO
        # THE ROSARY", "ON THE EUCHARIST IN ITS RELATIONSHIP TO THE CHURCH").
        cand = ADDRESS_RUNON.split(cand)[0].strip(" ,.;:")
        cand = SECTION_TAIL.sub("", cand).strip(" ,.;:")
        cand = TRAILING_MONTH.sub("", cand).strip(" ,.;:")
        cand = TRAILING_JUNK.sub("", cand).strip(" ,.;:")
        if 4 <= len(cand) <= 140:
            best = cand
            break

    stop = ADDRESSEE.search(after)
    body = after[stop.start():] if stop else after
    return best, re.sub(r"\s+", " ", body)[:2200]


def main():
    d = json.load(open(CACHE, encoding="utf-8"))
    hit = 0
    for slug, v in d.items():
        if "error" in v:
            continue
        subject, v["opening"] = parse(v.get("raw", ""))
        if subject:
            subject = strip_title_echo(subject, v["title"])
        v["subject"] = subject or MANUAL.get(slug)
        hit += bool(v["subject"])
    json.dump(d, open(CACHE, "w"), ensure_ascii=False, indent=1)
    total = len([v for v in d.values() if "error" not in v])
    print(f"subject lines: {hit}/{total}")
    for k in ("annum-sacrum", "arcanum-divinae", "cum-multa", "singulari-quadam",
              "iamdudum", "dum-multa", "mirae-caritatis"):
        if k in d:
            print(f"  {d[k]['title'][:24]:26} {d[k]['subject']!r}")

main()
