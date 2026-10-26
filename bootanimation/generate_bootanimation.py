#!/usr/bin/env python3
"""Generate an original Experience UI flowing-ribbon boot animation.

Modern glass-and-fluid visual direction; no Huawei/HarmonyOS logos or assets.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import math, zipfile, io

ROOT = Path(__file__).resolve().parent
OUT, PREVIEW = ROOT / "bootanimation.zip", ROOT / "experience-boot-preview.gif"
W, H, FPS, FRAME_COUNT = 720, 1280, 30, 48
NAVY, WHITE, MUTED = (4, 10, 30), (244, 249, 255), (154, 190, 238)

def font(size, bold=False):
    paths = (("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"),
             ("/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf"))
    for path in paths:
        try: return ImageFont.truetype(path, size)
        except OSError: pass
    return ImageFont.load_default()

def center(draw, text, y, fnt, fill):
    b = draw.textbbox((0, 0), text, font=fnt)
    draw.text(((W - (b[2] - b[0])) / 2, y), text, font=fnt, fill=fill)

base = Image.new("RGB", (W, H))
pix = base.load()
for y in range(H):
    t = y / (H - 1)
    col = tuple(int(NAVY[c] * (1-t) + (8, 33, 91)[c] * t) for c in range(3))
    for x in range(W): pix[x, y] = col

frames = []
brand, ui, sub = font(45, True), font(25, True), font(18)
for i in range(FRAME_COUNT):
    phase = i / FRAME_COUNT * 2 * math.pi
    im = Image.alpha_composite(base.convert("RGBA"), Image.new("RGBA", (W, H), (0,0,0,0)))
    glow = Image.new("RGBA", (W, H), (0,0,0,0))
    gd = ImageDraw.Draw(glow)
    gx, gy = int(W*(.5+.19*math.sin(phase))), int(H*(.39+.035*math.cos(phase*1.3)))
    gd.ellipse((gx-210, gy-210, gx+210, gy+210), fill=(20,118,255,92))
    glow = glow.filter(ImageFilter.GaussianBlur(90))
    im = Image.alpha_composite(im, glow)
    d = ImageDraw.Draw(im, "RGBA")
    cx, cy = W/2, H*.405
    for rx, ry, width, color, offset in [
        (150,104,12,(65,143,255,220),0.0),
        (118,148,8,(62,222,255,205),2.0),
        (178,78,5,(190,227,255,160),4.0)]:
        pts = []
        for k in range(121):
            a = k/120*2*math.pi
            wobble = 1+.08*math.sin(a*2+phase+offset)
            pts.append((cx+math.cos(a+phase*.35+offset)*rx*wobble,
                        cy+math.sin(a*1.15+phase*.4+offset)*ry*wobble))
        d.line(pts, fill=color, width=width, joint="curve")
    pulse = .75+.25*math.sin(phase)
    r = int(7+pulse*5)
    d.ellipse((cx-r,cy-r,cx+r,cy+r), fill=(225,249,255,245))
    alpha = int(255*min(1,max(0,(i-8)/12)))
    center(d, "experience", int(H*.59), brand, (*WHITE,alpha))
    center(d, "UI", int(H*.59)+51, ui, (100,196,255,alpha))
    center(d, "A fluid Android experience", int(H*.59)+100, sub, (*MUTED,int(alpha*.82)))
    frames.append(im.convert("RGB"))

with zipfile.ZipFile(OUT, "w", compression=zipfile.ZIP_STORED) as zf:
    zf.writestr("desc.txt", f"{W} {H} {FPS}\np 0 0 part0\n")
    for i, frame in enumerate(frames):
        buf=io.BytesIO(); frame.save(buf, format="PNG", optimize=True)
        zf.writestr(f"part0/{i:05d}.png", buf.getvalue())
preview=[f.resize((180,320)) for f in frames]
preview[0].save(PREVIEW, save_all=True, append_images=preview[1:], duration=1000//FPS, loop=0, optimize=True)
print(f"Generated {OUT} ({OUT.stat().st_size} bytes)")
print(f"Generated {PREVIEW} ({PREVIEW.stat().st_size} bytes)")
