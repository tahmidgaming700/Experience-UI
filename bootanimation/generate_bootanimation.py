#!/usr/bin/env python3
"""Generate a lightweight Android bootanimation.zip for Experience UI."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import math
import zipfile

ROOT = Path(__file__).resolve().parent
OUT = ROOT / "bootanimation.zip"
PREVIEW = ROOT / "experience-boot-preview.gif"
W, H = 2560, 1600
FPS = 30
FRAME_COUNT = 60
BG = (9, 13, 25)

def font(size):
    for candidate in (
        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
        "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
    ):
        try:
            return ImageFont.truetype(candidate, size)
        except OSError:
            pass
    return ImageFont.load_default()

title_font = font(112)
subtitle_font = font(48)
frame_images = []
for frame in range(FRAME_COUNT):
    im = Image.new("RGB", (W, H), BG)
    d = ImageDraw.Draw(im)
    cx, cy = W // 2, H // 2 - 120
    phase = 2 * math.pi * frame / FRAME_COUNT
    # Subtle orbit and segmented ring.
    d.ellipse((cx-230, cy-230, cx+230, cy+230), outline=(27, 39, 68), width=8)
    for i in range(24):
        angle = 2 * math.pi * i / 24 + phase
        alpha = (i / 23)
        color = (int(55 + 55*alpha), int(115 + 90*alpha), 255)
        r1, r2 = 195, 222
        x1, y1 = cx + math.cos(angle)*r1, cy + math.sin(angle)*r1
        x2, y2 = cx + math.cos(angle)*r2, cy + math.sin(angle)*r2
        d.line((x1, y1, x2, y2), fill=color, width=12)
    # Center mark: a simple geometric E, independent of trademarked assets.
    mark = [(cx-66, cy-75), (cx+60, cy-75), (cx+60, cy-45), (cx-30, cy-45),
            (cx-30, cy-15), (cx+39, cy-15), (cx+39, cy+15), (cx-30, cy+15),
            (cx-30, cy+45), (cx+60, cy+45), (cx+60, cy+75), (cx-66, cy+75)]
    d.polygon(mark, fill=(231, 238, 255))
    title = "EXPERIENCE UI"
    box = d.textbbox((0,0), title, font=title_font)
    d.text(((W-(box[2]-box[0]))/2, cy+310), title, font=title_font, fill=(238,243,255))
    subtitle = "A better Android experience"
    box = d.textbbox((0,0), subtitle, font=subtitle_font)
    d.text(((W-(box[2]-box[0]))/2, cy+450), subtitle, font=subtitle_font, fill=(139,158,202))
    frame_images.append(im)

with zipfile.ZipFile(OUT, "w", compression=zipfile.ZIP_STORED) as zf:
    zf.writestr("desc.txt", "2560 1600 30\np 0 0 part0\n")
    for i, im in enumerate(frame_images):
        import io
        buf = io.BytesIO()
        im.save(buf, format="PNG", optimize=True)
        zf.writestr(f"part0/{i:05d}.png", buf.getvalue())

frame_images[0].save(PREVIEW, save_all=True, append_images=frame_images[1:],
                     duration=1000//FPS, loop=0, optimize=True)
print(f"Generated {OUT} ({OUT.stat().st_size} bytes)")
print(f"Generated {PREVIEW} ({PREVIEW.stat().st_size} bytes)")
