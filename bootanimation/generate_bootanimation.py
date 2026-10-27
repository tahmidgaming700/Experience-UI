#!/usr/bin/env python3
"""Generate Experience UI's original hybrid-glass ribbon boot animation.

Visual direction: One UI 8.5-inspired polish, Liquid Glass-like layers,
and flowing glass ribbons. No proprietary vendor assets are used.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import math, zipfile, io

ROOT = Path(__file__).resolve().parent
OUT = ROOT / "bootanimation.zip"
PREVIEW = ROOT / "experience-boot-preview.gif"
W, H, FPS, FRAME_COUNT = 720, 1280, 30, 48
NAVY = (5, 10, 24)
WHITE = (244, 250, 255)
MUTED = (166, 199, 230)
CYAN = (116, 235, 239)
BLUE = (94, 166, 255)
VIOLET = (187, 157, 255)

def font(size, bold=False):
    paths = (
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
        "/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
    )
    for path in paths:
        try:
            return ImageFont.truetype(path, size)
        except OSError:
            pass
    return ImageFont.load_default()

def center(draw, text, y, fnt, fill):
    box = draw.textbbox((0, 0), text, font=fnt)
    draw.text(((W - (box[2] - box[0])) / 2, y), text, font=fnt, fill=fill)

def background():
    im = Image.new("RGB", (W, H))
    p = im.load()
    top, bottom = (5, 10, 24), (14, 27, 55)
    for y in range(H):
        t = y / (H - 1)
        c = tuple(round(top[i] * (1-t) + bottom[i] * t) for i in range(3))
        for x in range(W):
            p[x, y] = c
    return im

def ribbon_layer(phase, index):
    layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(layer, "RGBA")
    palette = [(CYAN[0], CYAN[1], CYAN[2], 90),
               (BLUE[0], BLUE[1], BLUE[2], 85),
               (VIOLET[0], VIOLET[1], VIOLET[2], 78)]
    offset = index * 2.05
    pts_outer, pts_inner = [], []
    for k in range(181):
        t = k / 180
        x = -150 + t * (W + 300)
        y = H * (0.32 + index * 0.17) + math.sin(t * math.pi * 2.1 + phase + offset) * (105 + index * 13)
        y += math.sin(t * math.pi * 4 + phase * .55) * 26
        pts_outer.append((x, y - 25 - index * 3))
        pts_inner.append((x, y + 25 + index * 3))
    polygon = pts_outer + list(reversed(pts_inner))
    d.polygon(polygon, fill=palette[index], outline=(221, 248, 255, 100))
    d.line(pts_outer, fill=(234, 251, 255, 115), width=3)
    d.line(pts_inner, fill=(164, 223, 255, 80), width=2)
    return layer.filter(ImageFilter.GaussianBlur(0.7))

def render_frame(i, base, brand, sub):
    phase = i / FRAME_COUNT * math.tau
    im = base.convert("RGBA")
    glow = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    gd = ImageDraw.Draw(glow)
    cx = int(W * (.5 + .08 * math.sin(phase)))
    cy = int(H * (.43 + .025 * math.cos(phase * 1.3)))
    gd.ellipse((cx-210, cy-210, cx+210, cy+210), fill=(63, 151, 255, 105))
    im = Image.alpha_composite(im, glow.filter(ImageFilter.GaussianBlur(80)))
    for idx in range(3):
        im = Image.alpha_composite(im, ribbon_layer(phase, idx))
    d = ImageDraw.Draw(im, "RGBA")
    cx, cy = W/2, H*.425
    pulse = .88 + .12 * math.sin(phase)
    for rx, ry, width, color, off in [
        (108, 78, 7, (218, 247, 255, 205), 0.0),
        (91, 117, 5, (107, 224, 255, 190), 2.1),
        (128, 57, 4, (208, 187, 255, 165), 4.2),
    ]:
        pts = []
        for k in range(121):
            a = k / 120 * math.tau
            wobble = 1 + .055 * math.sin(a*2 + phase + off)
            pts.append((cx + math.cos(a + phase*.25 + off)*rx*wobble,
                        cy + math.sin(a*1.1 + phase*.3 + off)*ry*wobble))
        d.line(pts, fill=color, width=width, joint="curve")
    r = int(7 + pulse * 4)
    d.ellipse((cx-r, cy-r, cx+r, cy+r), fill=(245, 253, 255, 245))
    alpha = int(255 * min(1, max(0, (i-5)/10)))
    center(d, "EXPERIENCE UI", int(H*.59), brand, (*WHITE, alpha))
    center(d, "HYBRID GLASS", int(H*.59)+56, sub, (*MUTED, int(alpha*.86)))
    return im.convert("RGB")

def main():
    base = background()
    brand, sub = font(35, True), font(18, True)
    frames = [render_frame(i, base, brand, sub) for i in range(FRAME_COUNT)]
    with zipfile.ZipFile(OUT, "w", compression=zipfile.ZIP_STORED) as zf:
        zf.writestr("desc.txt", f"{W} {H} {FPS}\np 0 0 part0\n")
        for i, frame in enumerate(frames):
            buf = io.BytesIO()
            frame.save(buf, format="PNG", optimize=True)
            zf.writestr(f"part0/{i:05d}.png", buf.getvalue())
    preview = [f.resize((180, 320)) for f in frames]
    preview[0].save(PREVIEW, save_all=True, append_images=preview[1:],
                    duration=1000//FPS, loop=0, optimize=True)
    with zipfile.ZipFile(OUT) as zf:
        assert zf.testzip() is None
        assert zf.read("desc.txt").decode().splitlines()[0].split() == ["720", "1280", "30"]
        assert len([n for n in zf.namelist() if n.startswith("part0/") and n.endswith(".png")]) == FRAME_COUNT
    print(f"Generated {OUT} ({OUT.stat().st_size} bytes)")
    print(f"Generated {PREVIEW} ({PREVIEW.stat().st_size} bytes)")

if __name__ == "__main__":
    main()
