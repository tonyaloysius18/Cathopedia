# Renders the three Bible layout proposals as full-size phone mockups (HTML -> PNG via headless Chrome).
import subprocess, pathlib
HERE = pathlib.Path(__file__).parent
CSS = """
*{box-sizing:border-box;margin:0;padding:0}
body{background:#e9e4d8;font-family:-apple-system,Roboto,Helvetica,sans-serif;padding:36px 40px}
h1{font:600 34px Georgia,serif;color:#1F4034}
.sub{font-size:19px;color:#4a5550;margin:6px 0 26px;max-width:1150px;line-height:1.4}
.row{display:flex;gap:34px}
.col{width:390px}
.lbl{font:600 18px -apple-system,sans-serif;color:#1F4034;margin-bottom:10px}
.ph{width:390px;height:844px;border-radius:46px;border:10px solid #1c1c1c;background:#0E1A15;color:#EDE6D3;position:relative;overflow:hidden;font-size:15px}
.sb{height:44px;display:flex;justify-content:space-between;align-items:center;padding:0 26px;font-size:14px}
.in{padding:0 18px}
.sf{font-family:Georgia,'Noto Serif',serif}
.g{color:#D4AF37}.mu{color:#9FAEA6}
.cap{font-size:12px;letter-spacing:1.4px;color:#D4AF37;font-weight:600}
.hdr{background:#14231d;border-radius:0 0 22px 22px;padding:8px 20px 16px;margin-bottom:12px}
.bar{position:absolute;left:14px;right:14px;bottom:22px;height:56px;border-radius:28px;border:1.5px solid #8a7430;background:#17251f;display:flex;align-items:center;justify-content:space-around;padding:0 20px}
.c{width:36px;height:36px;border-radius:50%;border:2px solid #D4AF37;background:#1F4034}
.big{width:56px;height:56px;margin-top:-34px;border:3px solid #D4AF37;background:#1F4034;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:24px;color:#D4AF37}
.home{position:absolute;bottom:6px;left:50%;width:130px;height:5px;margin-left:-65px;border-radius:3px;background:#EDE6D3}
"""
def bar(): return '<div class="bar"><span class="c"></span><span class="c"></span><span class="big">✝</span><span class="c"></span><span class="c"></span></div><div class="home"></div>'
def sb(dark=True): return f'<div class="sb" style="color:{"#EDE6D3" if dark else "#2b2416"}"><b>10:40</b><span>▂▄▆ ◔ ▮</span></div>'
def phone(label, body, style=""):
    return f'<div class="col"><div class="lbl">{label}</div><div class="ph" style="{style}">{body}</div></div>'

# ---------- A: Illuminated library ----------
gosp = [("Mt","Matthew",28),("Mk","Mark",16),("Lk","Luke",24),("Jn","John",21)]
paul = [("Rm","Romans",16),("1Co","1 Corinthians",16),("2Co","2 Corinthians",13),("Ga","Galatians",6)]
tile = lambda a,n,c: f'<div style="border:1px solid #3d4a3a;border-radius:12px;padding:10px 12px;background:#121f19"><div class="g sf" style="font-size:20px">{a}</div><div class="sf" style="font-size:15px;margin-top:2px">{n}</div><div class="mu" style="font-size:12px">{c} chapters</div></div>'
A1 = sb()+f'''<div class="in"><div class="sf" style="font-size:30px;margin-top:6px">Holy Bible</div><div class="cap" style="margin-top:4px">WORLD ENGLISH BIBLE · 73 BOOKS</div>
<div style="margin-top:14px;height:44px;border-radius:22px;border:1px solid #3d4a3a;display:flex;align-items:center;padding:0 16px" class="mu"><span class="g" style="margin-right:10px">⌕</span>Search the Bible</div>
<div style="margin-top:14px;border:1.5px solid #D4AF37;border-radius:16px;padding:14px;background:#1F4034">
<div class="cap">CONTINUE READING</div><div class="sf" style="font-size:22px;margin:6px 0 4px">Matthew 13</div>
<div class="sf mu" style="font-style:italic;font-size:14px">“Behold, a farmer went out to sow…”</div>
<div style="height:4px;background:#0E1A15;border-radius:2px;margin-top:10px"><div style="width:46%;height:4px;background:#D4AF37;border-radius:2px"></div></div></div>
<div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-top:12px">
<div style="height:88px;border-radius:14px;background:#3a2a14;border:1.5px solid #D4AF37;padding:12px"><div class="sf" style="font-size:17px">Old Testament</div><div class="mu" style="font-size:13px;margin-top:4px">46 books</div></div>
<div style="height:88px;border-radius:14px;background:#4a1c1c;border:1.5px solid #D4AF37;padding:12px;box-shadow:0 0 0 3px #D4AF3755"><div class="sf" style="font-size:17px">New Testament</div><div class="mu" style="font-size:13px;margin-top:4px">27 books</div></div></div>
<div class="cap" style="margin-top:16px">THE GOSPELS</div>
<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-top:8px">{"".join(tile(*b) for b in gosp)}</div>
<div class="cap" style="margin-top:14px">LETTERS OF SAINT PAUL</div>
<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-top:8px">{"".join(tile(*b) for b in paul[:2])}</div></div>'''+bar()
cells = "".join(f'<div style="aspect-ratio:1;border-radius:10px;display:flex;align-items:center;justify-content:center;font-size:16px;{"background:#D4AF37;color:#0E1A15;font-weight:700;" if i==12 else ("background:#1F4034;color:#D4AF37;" if i<12 else "border:1px solid #3d4a3a;")}">{i+1}</div>' for i in range(28))
A2 = sb()+f'''<div class="hdr"><div style="display:flex;gap:12px;align-items:center"><span class="c" style="display:flex;align-items:center;justify-content:center;color:#D4AF37">‹</span><div><div class="sf" style="font-size:28px">Matthew</div><div class="cap">28 CHAPTERS · 12 READ</div></div></div></div>
<div class="in"><div style="display:grid;grid-template-columns:repeat(6,1fr);gap:8px">{cells}</div>
<div style="display:flex;gap:14px;margin-top:16px;font-size:13px" class="mu"><span><span style="display:inline-block;width:12px;height:12px;border-radius:3px;background:#1F4034;vertical-align:-1px"></span> Read</span><span><span style="display:inline-block;width:12px;height:12px;border-radius:3px;background:#D4AF37;vertical-align:-1px"></span> Where you stopped</span></div>
<div style="margin-top:20px;border:1px solid #3d4a3a;border-radius:14px;padding:14px"><div class="cap">ABOUT THIS BOOK</div><div class="sf" style="font-size:14px;margin-top:6px;line-height:1.5">The first Gospel, written for Jewish Christians, presents Jesus as the promised Messiah and new Moses.</div></div></div>'''+bar()
A3 = sb(False)+'''<div style="text-align:center;margin-top:10px" class="sf"><div style="font-size:12px;letter-spacing:4px;color:#8a6d1e">THE GOSPEL ACCORDING TO</div><div style="font-size:22px;color:#2b2416;margin-top:2px">Matthew</div><div style="font-size:52px;color:#8a6d1e;line-height:1.1">13</div><div style="color:#8a6d1e">✦ ✦ ✦</div></div>
<div class="sf" style="padding:6px 24px;color:#2b2416;font-size:17px;line-height:1.7;text-align:justify"><span style="float:left;font-size:62px;line-height:54px;color:#8a1c1c;margin:4px 6px 0 0">O</span>n that day Jesus went out of the house and sat by the seaside. <sup style="color:#8a6d1e;font-size:11px">2</sup>Great multitudes gathered to him, so that he entered into a boat and sat; and all the multitude stood on the beach. <sup style="color:#8a6d1e;font-size:11px">3</sup>He spoke to them many things in parables, saying, “Behold, a farmer went out to sow. <sup style="color:#8a6d1e;font-size:11px">4</sup>As he sowed, some seeds fell by the roadside, and the birds came and devoured them.</div>
<div style="position:absolute;left:40px;right:40px;bottom:96px;height:44px;border-radius:22px;background:#2b2416;color:#efe6cf;display:flex;justify-content:space-between;align-items:center;padding:0 18px;font-size:15px"><span>‹ 12</span><span class="sf">Aa</span><span>☆</span><span>14 ›</span></div>'''+bar()

# ---------- B: Quick navigator ----------
books = ["Matthew","Mark","Luke","John","Acts of the Apostles","Romans","1 Corinthians","2 Corinthians","Galatians","Ephesians"]
abbr = ["Mt","Mk","Lk","Jn","Ac","Rm","1Co","2Co","Ga","Ep"]
chip = lambda t,on=False: f'<span style="border:1.5px solid {"#D4AF37" if on else "#3d4a3a"};border-radius:16px;padding:6px 12px;font-size:13px;white-space:nowrap;{"color:#0E1A15;background:#D4AF37;font-weight:600" if on else ""}">{t}</span>'
rows = "".join(f'<div style="display:flex;align-items:center;gap:12px;padding:11px 0;border-bottom:1px solid #1d2b25"><span style="width:36px;height:36px;border-radius:50%;background:#1F4034;border:1px solid #6b5b2a;color:#D4AF37;font-size:12px;display:flex;align-items:center;justify-content:center">{a}</span><span class="sf" style="font-size:17px;flex:1">{b}</span><span class="g">›</span></div>' for a,b in zip(abbr,books))
B1 = sb()+f'''<div class="in"><div style="display:flex;align-items:center;justify-content:space-between;margin-top:6px"><div class="sf" style="font-size:28px">Holy Bible</div><span style="width:40px;height:40px;border-radius:50%;border:1.5px solid #D4AF37;display:flex;align-items:center;justify-content:center" class="g">⌕</span></div>
<div style="display:flex;border:1.5px solid #D4AF37;border-radius:22px;overflow:hidden;margin-top:12px;font-size:15px"><div style="flex:1;text-align:center;padding:9px">Old Testament</div><div style="flex:1;text-align:center;padding:9px;background:#D4AF37;color:#0E1A15;font-weight:600">New Testament</div></div>
<div style="display:flex;gap:7px;margin-top:12px;overflow:hidden">{chip("Gospels",True)}{chip("Acts")}{chip("Saint Paul")}{chip("Letters")}{chip("Revelation")}</div>
<div style="margin-top:6px">{rows}</div></div>'''+bar()
nums = "".join(f'<div style="aspect-ratio:1;border-radius:50%;border:1.5px solid {"#D4AF37" if i==12 else "#3d4a3a"};display:flex;align-items:center;justify-content:center;font-size:15px;{"background:#D4AF37;color:#0E1A15;font-weight:700" if i==12 else ""}">{i+1}</div>' for i in range(28))
B2 = sb()+f'''<div class="in" style="opacity:.3"><div class="sf" style="font-size:28px;margin-top:6px">Holy Bible</div>{rows}</div>
<div style="position:absolute;left:0;right:0;bottom:0;height:520px;background:#17251f;border-radius:26px 26px 0 0;border-top:2px solid #D4AF37;padding:12px 20px">
<div style="width:44px;height:5px;background:#6b5b2a;border-radius:3px;margin:0 auto 14px"></div>
<div style="display:flex;justify-content:space-between;align-items:baseline"><div class="sf" style="font-size:24px">Matthew</div><div class="mu" style="font-size:13px">28 chapters</div></div>
<div style="display:grid;grid-template-columns:repeat(6,1fr);gap:9px;margin-top:14px">{nums}</div>
<div class="mu" style="text-align:center;margin-top:16px;font-size:13px">Pull down to close</div></div>'''
verse = lambda n,t,hl=False: f'<div style="display:flex;gap:10px;padding:8px 10px;border-radius:10px;{"background:#1F4034;" if hl else ""}"><span class="g" style="font-size:13px;min-width:16px;margin-top:3px">{n}</span><span class="sf" style="font-size:17px;line-height:1.55;flex:1">{t}</span>{"<span class=g>★</span>" if hl else ""}</div>'
B3 = sb()+f'''<div class="hdr" style="display:flex;gap:8px;align-items:center"><span style="border:1.5px solid #D4AF37;border-radius:18px;padding:8px 14px;font-size:16px" class="sf">Matthew ▾</span><span style="border:1.5px solid #D4AF37;border-radius:18px;padding:8px 14px;font-size:16px">13 ▾</span><span style="margin-left:auto;border:1.5px solid #3d4a3a;border-radius:18px;padding:8px 12px" class="g sf">Aa</span></div>
<div class="in">{verse(1,"On that day Jesus went out of the house and sat by the seaside.")}{verse(2,"Great multitudes gathered to him, so that he entered into a boat and sat; and all the multitude stood on the beach.",True)}{verse(3,"He spoke to them many things in parables, saying, “Behold, a farmer went out to sow.")}{verse(4,"As he sowed, some seeds fell by the roadside, and the birds came and devoured them.")}{verse(5,"Others fell on rocky ground, where they didn’t have much soil…")}</div>
<div class="mu" style="position:absolute;bottom:92px;left:0;right:0;text-align:center;font-size:13px">‹ swipe sideways for chapter 12 / 14 ›</div>'''+bar()

# ---------- C: Daily reading first ----------
prog = lambda b,ch,tot: f'<div style="flex:1;border:1px solid #3d4a3a;border-radius:14px;padding:12px;background:#121f19"><div class="sf" style="font-size:17px">{b}</div><div class="mu" style="font-size:13px;margin-top:3px">Chapter {ch} of {tot}</div><div style="height:4px;background:#2a3a33;border-radius:2px;margin-top:10px"><div style="width:{int(ch/tot*100)}%;height:4px;background:#D4AF37;border-radius:2px"></div></div></div>'
C1 = sb()+f'''<div class="in"><div class="sf" style="font-size:28px;margin-top:6px">Holy Bible</div>
<div style="margin-top:14px;border-radius:18px;background:#1F4034;padding:18px;border:1.5px solid #D4AF37">
<div class="cap">VERSE OF THE DAY</div>
<div class="sf" style="font-size:19px;margin:10px 0;line-height:1.45">“For God so loved the world, that he gave his only born Son, that whoever believes in him should not perish, but have eternal life.”</div>
<div style="display:flex;justify-content:space-between"><span class="g sf">John 3:16</span><span class="g">☆  ↗</span></div></div>
<div class="cap" style="margin-top:18px">KEEP READING</div>
<div style="display:flex;gap:10px;margin-top:8px">{prog("Matthew",13,28)}{prog("Psalms",23,150)}</div>
<div class="cap" style="margin-top:18px">BOOKMARKS</div>
<div class="sf" style="margin-top:8px;font-size:15px;line-height:1.9">★ John 3:16<br>★ Luke 1:46</div>
<div style="margin-top:14px;border:1.5px solid #D4AF37;border-radius:14px;padding:14px;display:flex;justify-content:space-between;font-size:16px"><span class="sf">Browse all 73 books</span><span class="g">›</span></div></div>'''+bar()
heads = [("1","The genealogy of Jesus Christ",True),("2","The wise men from the east",True),("3","John the Baptizer",True),("4","Temptation in the wilderness",True),("5","The Beatitudes",True),("6","The Lord’s Prayer",True),("7","The house on the rock",False),("8","Healing of a leper",False),("9","The call of Matthew",False)]
hl = "".join(f'<div style="display:flex;gap:12px;align-items:center;padding:11px 0;border-bottom:1px solid #1d2b25"><span class="g sf" style="width:22px;font-size:17px">{n}</span><span class="sf" style="flex:1;font-size:16px">{t}</span><span style="color:{"#D4AF37" if r else "#3d4a3a"}">{"✓" if r else "○"}</span></div>' for n,t,r in heads)
C2 = sb()+f'''<div class="hdr"><div style="display:flex;gap:14px;align-items:center"><div style="width:58px;height:58px;border-radius:50%;background:conic-gradient(#D4AF37 0 32%,#2a3a33 0);display:flex;align-items:center;justify-content:center"><div style="width:46px;height:46px;border-radius:50%;background:#14231d;display:flex;align-items:center;justify-content:center;font-size:13px" class="g">32%</div></div><div><div class="sf" style="font-size:28px">Matthew</div><div class="cap">9 OF 28 CHAPTERS READ</div></div></div></div>
<div class="in">{hl}</div>'''+bar()
C3 = sb()+'''<div style="padding:30px 26px 0"><div class="sf g" style="font-size:15px;text-align:center;letter-spacing:2px">MATTHEW 13</div>
<div class="sf" style="font-size:18px;line-height:1.8;margin-top:16px"><sup class="g" style="font-size:11px">1</sup> On that day Jesus went out of the house and sat by the seaside. <sup class="g" style="font-size:11px">2</sup> Great multitudes gathered to him, so that he entered into a boat and sat; and all the multitude stood on the beach. <sup class="g" style="font-size:11px">3</sup> He spoke to them many things in parables, saying, “Behold, a farmer went out to sow. <sup class="g" style="font-size:11px">4</sup> As he sowed, some seeds fell by the roadside, and the birds came and devoured them.</div></div>
<div style="position:absolute;left:26px;right:26px;bottom:100px"><div style="display:flex;justify-content:space-between;font-size:13px" class="mu"><span>Matthew 13 · 46%</span><span>Tap for tools</span></div>
<div style="height:4px;background:#2a3a33;margin-top:8px;border-radius:2px"><div style="width:46%;height:4px;background:#D4AF37;border-radius:2px"></div></div></div>'''+bar()

OPTS = [
 ("A","Illuminated library","Book-like and rich: big cover tiles for each Testament, books as a 2-column tile grid, chapters that show what you have read, and a parchment reading page with flowing paragraphs and a red drop cap.",[("Home",A1,""),("Chapters",A2,""),("Reader",A3,"background:#efe6cf")]),
 ("B","Quick navigator","Fastest to move around: a Testament switch with jump chips, a compact book list, chapters in a sheet that slides up (no extra screen), and Book ▾ / Chapter ▾ menus in the reader with swipe for next chapter.",[("Home",B1,""),("Chapters (slide-up sheet)",B2,""),("Reader",B3,"")]),
 ("C","Daily reading first","Built around a daily habit: verse of the day, reading progress per book, chapters listed with their headings and a tick when read, and a calm distraction-free reader.",[("Home",C1,""),("Chapters",C2,""),("Reader",C3,"")]),
]
chrome = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
for key,name,desc,phones in OPTS:
    html = f'<!doctype html><meta charset=utf-8><style>{CSS}</style><h1>Option {key} · {name}</h1><div class="sub">{desc}</div><div class="row">{"".join(phone(*p) for p in phones)}</div>'
    f = HERE/f"option-{key}.html"; f.write_text(html)
    subprocess.run([chrome,"--headless=new","--disable-gpu","--hide-scrollbars","--force-device-scale-factor=1.5",f"--screenshot={HERE}/option-{key}.png","--window-size=1350,1060",f.as_uri()],check=True,capture_output=True)
    print("wrote", f"option-{key}.png")
