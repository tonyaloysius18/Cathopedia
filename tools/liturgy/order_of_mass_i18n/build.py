"""Builds order_of_mass.<lang>.json from the English file plus a per-language module in this folder.

Each <lang>.py holds T = {top, sections, parts[, lords_prayer]}: Cathopedia's own guidance text, translated.
The Sign of the Cross and the Our Father are taken from the app's prayer content (Amen dropped, as at Mass),
unless a module gives its own lords_prayer (French uses the liturgical Notre Pere).

Usage: python3 tools/liturgy/order_of_mass_i18n/build.py fr it de es pt nl pl ta
"""
import json, sys, importlib.util, os
ROOT=os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
OUT=ROOT+'/shared/src/commonMain/composeResources/files/content/ordo/'
en=json.load(open(OUT+'order_of_mass.en.json'))
prayers={s:json.load(open(f'{ROOT}/content/prayers/{s}.json'))['text'] for s in ['sign-of-the-cross','our-father']}
def strip_amen(t):
    lines=t.strip().split('\n')
    if lines[-1].strip().rstrip('.').lower() in ('amen','amén','amém','ஆமென்'): lines=lines[:-1]
    return '\n'.join(lines)
for lang in sys.argv[1:]:
    spec=importlib.util.spec_from_file_location(lang, os.path.join(os.path.dirname(__file__), f'{lang}.py'))
    m=importlib.util.module_from_spec(spec); spec.loader.exec_module(m)
    T=m.T
    doc=json.loads(json.dumps(en)); doc['language']=lang
    for k in ['note','missalPending','readingPending','readingStudyNote','homilyPending','goodFriday','holySaturday']:
        doc[k]=T['top'][k]
    for s in doc['sections']:
        s['title']=T['sections'][s['id']]
        for p in s['parts']:
            tr=T['parts'][p['id']]
            for f in ['title','who','rubric','rubricLent','missal','textNote']:
                if f in p: p[f]=tr[f]
            missing=[f for f in ['title','who','rubric','rubricLent','missal','textNote'] if f in p and f not in tr]
            assert not missing,(lang,p['id'],missing)
    parts={p['id']:p for s in doc['sections'] for p in s['parts']}
    parts['sign_of_cross']['text']=prayers['sign-of-the-cross'][lang]['bodyMd'].strip()
    parts['lords_prayer']['text']=T.get('lords_prayer') or strip_amen(prayers['our-father'][lang]['bodyMd'])
    open(OUT+f'order_of_mass.{lang}.json','w').write(json.dumps(doc,ensure_ascii=False,indent=2)+'\n')
    print('wrote',lang)
