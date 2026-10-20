import json, numpy as np
from PIL import Image, ImageFilter, ImageDraw, ImageFont
from scipy import ndimage
faces=json.load(open('faces.json')); meta=json.load(open('meta.json'))
W,H=320,400
FACE_OVERRIDE={'francesco_coccopalmerio':1}
TIGHT={'anthony_olubunmi_okogie':2.1}
SMALLEST_INSTANCE=set()
# Where the cut-out fails (white on white, crowds), fade the photo into the same backdrop instead.
VIGNETTE={'filipe_neri_ferrao','anthony_olubunmi_okogie','christophe_pierre','jean_zerbo','adalberto_martinez_flores'}
TIGHT_OVAL={}
# Mural behind him reads as a person: keep the part of the mask that contains his chest.
ANCHOR={}
# One backdrop for every portrait: deep cardinal red, lit softly behind the head.
yy,xx=np.mgrid[0:H,0:W]
d=np.sqrt(((xx-W*0.5)/(W*0.75))**2+((yy-H*0.33)/(H*0.70))**2).clip(0,1)
c0=np.array([168,32,44]); c1=np.array([58,6,14])
backdrop=(c0*(1-d[...,None])+c1*d[...,None])
backdrop*= (1-0.25*(yy/H))[...,None]**0.5
backdrop=backdrop.clip(0,255)
def crop_box(iw,ih,fs,cid):
    if fs:
        f=fs[FACE_OVERRIDE[cid]] if cid in FACE_OVERRIDE else max(fs,key=lambda f:f[2]*f[3])
        x,y,w,h,_=f; fx,fy,fw,fh=x*iw,y*ih,w*iw,h*ih
        k=TIGHT.get(cid, 3.2 if cid in FACE_OVERRIDE else 2.9)
        cw=fw*k; ch=cw*1.25
        if cw>iw: cw=iw; ch=cw*1.25
        if ch>ih: ch=ih; cw=ch/1.25
        cx=fx+fw/2-(fw*0.35 if cid in FACE_OVERRIDE else 0); top=fy+fh/2-(0.42 if cid in FACE_OVERRIDE else 0.40)*ch
        face=(fx,fy,fw,fh)
    else:
        cw=min(iw,ih/1.25); ch=cw*1.25; cx=iw/2; top=0; face=None
    left=min(max(cx-cw/2,0),iw-cw); top=min(max(top,0),ih-ch)
    return (round(left),round(top),round(left+cw),round(top+ch)),face
report=[]
for m in meta:
    cid=m['id']
    im=Image.open('raw/'+cid).convert('RGBA'); bg=Image.new('RGB',im.size,(255,255,255)); bg.paste(im,mask=im.split()[3]); im=bg
    mask=Image.open(f'masks/{cid}.png').convert('L').resize(im.size)
    iw,ih=im.size; box,face=crop_box(iw,ih,faces.get(cid,[]),cid)
    a=np.asarray(mask,dtype=np.float32)/255
    import glob
    inst=[np.asarray(Image.open(p).convert('L').resize(im.size),dtype=np.float32)/255 for p in sorted(glob.glob(f'imasks/{cid}__*.png'))]
    if inst and cid not in ANCHOR:
        if face:
            fx,fy,fw,fh=[int(v) for v in face]
            score=[float(i[max(fy,0):fy+fh, max(fx,0):fx+fw].mean()) for i in inst]
        else:
            score=[float(i.mean()) for i in inst]
        if cid in SMALLEST_INSTANCE:
            # A mural behind him also reads as a person: take the smallest one covering his face.
            ok=[k for k,v in enumerate(score) if v>0.5]
            pick=min(ok,key=lambda k:inst[k].mean()) if ok else int(np.argmax(score))
        else:
            pick=int(np.argmax(score))
        chosen=inst[pick]>0.5
        # Fine edges from the accurate mask, limited to the one person whose face we cropped on.
        a=a*ndimage.binary_dilation(chosen,iterations=8)
    lab,n=ndimage.label(a>0.5)
    if n>1:
        sizes=ndimage.sum(np.ones_like(lab),lab,range(1,n+1)); keep=int(np.argmax(sizes))+1
        a=a*ndimage.binary_dilation(lab==keep,iterations=4)
    if cid in ANCHOR:
        base=np.asarray(mask,dtype=np.float32)/255
        lab2,_=ndimage.label(base>0.5); ax,ay=ANCHOR[cid]
        # The only "person" Vision separates here is the mural: subtract it from the full mask.
        mural=np.asarray(Image.open(f'imasks/{cid}__1.png').convert('L').resize(im.size),dtype=np.float32)/255>0.5
        base=base*(1-ndimage.binary_dilation(mural,iterations=3))
        # Erode first so thin bridges to the mural break, then grow his region back.
        core=ndimage.binary_erosion(base>0.5,iterations=14)
        lab2,_=ndimage.label(core); keep2=lab2[int(ay*ih),int(ax*iw)]
        a=base*ndimage.binary_dilation(lab2==keep2,iterations=18) if keep2 else base
    soft=Image.fromarray((a*255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(1.2))
    c=im.crop(box).resize((W,H),Image.LANCZOS); ms=soft.crop(box).resize((W,H),Image.LANCZOS)
    alpha=np.asarray(ms,dtype=np.float32)[...,None]/255
    if cid in VIGNETTE:
        cx_,cy_,rx_,ry_=TIGHT_OVAL.get(cid,(0.5,0.40,0.50,0.58))
        r=np.sqrt(((xx-W*cx_)/(W*rx_))**2+((yy-H*cy_)/(H*ry_))**2)
        oval=np.clip((1.0-r)/0.35,0,1)[...,None]
        alpha=oval
    out=(np.asarray(c,dtype=np.float32)*alpha+backdrop*(1-alpha)).clip(0,255).astype(np.uint8)
    Image.fromarray(out).save(f'red/{cid}.webp','WEBP',quality=82,method=6)
    cover=float(alpha.mean())
    facecov=None
    if face:
        fx,fy,fw,fh=face; l,t,r,b=box; sx=W/(r-l); sy=H/(b-t)
        fa=alpha[int((fy-t)*sy):int((fy+fh-t)*sy), int((fx-l)*sx):int((fx+fw-l)*sx)]
        facecov=float(fa.mean()) if fa.size else None
    flag = cover<0.15 or cover>0.92 or (facecov is not None and facecov<0.6) or face is None
    report.append((cid,round(cover,2),facecov and round(facecov,2),flag))
json.dump(report,open('red_report.json','w'))
flagged=[r for r in report if r[3]]; print('flagged',len(flagged)); print(flagged)
font=ImageFont.load_default()
for s in range(0,len(meta),40):
    sheet=Image.new('RGB',(8*170,5*230),'white'); dr=ImageDraw.Draw(sheet)
    for i,m in enumerate(meta[s:s+40]):
        t=Image.open(f"red/{m['id']}.webp").resize((160,200)); X,Y=(i%8)*170+5,(i//8)*230+5
        sheet.paste(t,(X,Y)); dr.text((X,Y+202),f"{s+i+1} {m['title'][:24]}",fill='black',font=font)
        if report[s+i][3]: dr.rectangle((X,Y,X+159,Y+199),outline='lime',width=3)
    sheet.save(f"sheets_red/sheet{s//40+1}.jpg",quality=85)
