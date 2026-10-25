#!/usr/bin/env python3
"""Generate the eight-stage Experience UI boot animation from the project sketch."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import math
import zipfile
import io

ROOT = Path(__file__).resolve().parent
OUT = ROOT / "bootanimation.zip"
PREVIEW = ROOT / "experience-boot-preview.gif"
W, H = 1080, 1920
FPS = 24
STAGES = 8
FRAMES_PER_STAGE = 8
FRAME_COUNT = STAGES * FRAMES_PER_STAGE
BG_TOP = (5, 18, 52)
BG_BOTTOM = (8, 54, 119)
WHITE = (239, 246, 255)
BLUE = (79, 163, 255)
MUTED = (158, 193, 244)

def font(size, bold=False):
    candidates = (
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
        "/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
    )
    for candidate in candidates:
        try:
            return ImageFont.truetype(candidate, size)
        except OSError:
            pass
    return ImageFont.load_default()

label_font = font(28, True)
brand_font = font(74, True)
sub_font = font(34)
small_font = font(24)
frames = []

def centered(draw, text, y, fnt, fill):
    box = draw.textbbox((0, 0), text, font=fnt)
    draw.text(((W - (box[2] - box[0])) / 2, y), text, font=fnt, fill=fill)

for index in range(FRAME_COUNT):
    stage = index // FRAMES_PER_STAGE + 1
    local = (index % FRAMES_PER_STAGE) / (FRAMES_PER_STAGE - 1)
    phase = index / FRAME_COUNT
    im = Image.new("RGB", (W, H))
    px = im.load()
    # Rich blue vertical gradient with a subtle moving cyan glow.
    glow_x = int(W * (0.25 + 0.5 * ((math.sin(phase * 2 * math.pi) + 1) / 2)))
    for y in range(H):
        t = y / H
        base = tuple(int(BG_TOP[c] * (1-t) + BG_BOTTOM[c] * t) for c in range(3))
        for x in range(W):
            dist = math.sqrt(((x-glow_x)/W)**2 + ((y-H*0.42)/H)**2)
            glow = max(0.0, 1.0 - dist * 2.7) * 0.20
            px[x, y] = tuple(min(255, int(base[c] + (50, 125, 210)[c] * glow)) for c in range(3))
    d = ImageDraw.Draw(im, "RGBA")
    # A luminous orbital line evokes the flowing ribbon in the reference home screen.
    orbit_box = (90, 420, W-90, 420 + 740)
    d.arc(orbit_box, start=190 + int(phase*360), end=505 + int(phase*360), fill=(77, 177, 255, 150), width=5)
    d.arc((150, 470, W-150, 1060), start=10 - int(phase*240), end=300 - int(phase*240), fill=(120, 145, 255, 95), width=3)
    centered(d, f"STAGE {stage:02d}", 230, label_font, MUTED)
    cx, cy = W//2, 760
    # Progressive mark construction, intentionally following the user's 01-08 sketch.
    if stage >= 1:
        d.ellipse((cx-105, cy-105, cx+105, cy+105), outline=(105, 180, 255, 150), width=5)
        d.ellipse((cx-76, cy-76, cx+76, cy+76), outline=(210, 233, 255, 90), width=2)
    if stage == 1:
        d.arc((cx-45, cy-50, cx+45, cy+50), 75, 285, fill=WHITE, width=16)
        d.line((cx+22, cy-2, cx+22, cy+52), fill=WHITE, width=15)
    elif stage == 2:
        centered(d, "HARMONY", cy-34, font(54, True), WHITE)
    elif stage == 3:
        d.line((cx-65, cy+70, cx+65, cy-75), fill=WHITE, width=17)
    elif stage == 4:
        d.line((cx-80, cy+80, cx+80, cy-80), fill=WHITE, width=17)
        d.line((cx-55, cy-70, cx-28, cy-40), fill=BLUE, width=11)
    elif stage == 5:
        d.line((cx-82, cy+80, cx+82, cy-80), fill=WHITE, width=18)
        d.line((cx-52, cy-70, cx-25, cy-40), fill=BLUE, width=12)
        d.line((cx+28, cy+42, cx+55, cy+70), fill=BLUE, width=12)
    elif stage == 6:
        d.line((cx-62, cy-42, cx-28, cy+42), fill=WHITE, width=18)
        d.line((cx+62, cy-42, cx+28, cy+42), fill=WHITE, width=18)
    elif stage >= 7:
        centered(d, "experience ui", cy-35, brand_font, WHITE)
        if stage >= 8:
            centered(d, "powered by", cy+80, sub_font, MUTED)
            # Android-like simple dot-and-antenna glyph, drawn from primitives rather than a logo asset.
            ax, ay = cx-100, cy+170
            d.arc((ax-24, ay-20, ax+24, ay+25), 180, 360, fill=(180, 215, 255, 255), width=4)
            d.ellipse((ax-13, ay-7, ax-5, ay+1), fill=(180, 215, 255, 255))
            d.ellipse((ax+5, ay-7, ax+13, ay+1), fill=(180, 215, 255, 255))
            centered(d, "Android", cy+145, font(42, True), WHITE)
    if stage < 7:
        centered(d, "EXPERIENCE UI", 1190, font(38, True), WHITE)
    else:
        centered(d, "A different kind of Android", 1190, small_font, MUTED)
    # Fade each stage in smoothly without changing the intended eight-stage sequence.
    if local < 0.35:
        overlay = Image.new("RGB", (W, H), BG_TOP)
        im = Image.blend(overlay, im, 0.35 + 0.65 * (local / 0.35))
    frames.append(im)

with zipfile.ZipFile(OUT, "w", compression=zipfile.ZIP_STORED) as zf:
    zf.writestr("desc.txt", f"{W} {H} {FPS}\np 1 0 part0\n")
    for i, frame in enumerate(frames):
        buf = io.BytesIO()
        frame.save(buf, format="PNG", optimize=True)
        zf.writestr(f"part0/{i:05d}.png", buf.getvalue())

# Compact preview for GitHub Actions artifact; ZIP retains full-resolution frames.
preview_frames = [frame.resize((270, 480)) for frame in frames]
preview_frames[0].save(PREVIEW, save_all=True, append_images=preview_frames[1:],
                       duration=1000//FPS, loop=0, optimize=True)
print(f"Generated {OUT} ({OUT.stat().st_size} bytes)")
print(f"Generated {PREVIEW} ({PREVIEW.stat().st_size} bytes)")
