#!/usr/bin/env bash
set -euo pipefail

BASE_ISO="${1:?Usage: repack-android-x86.sh BASE.iso OUTPUT.iso WORK_ROOT}"
OUTPUT_ISO="${2:?Missing output ISO path}"
WORK_ROOT="${3:?Missing work directory}"
REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"

for tool in xorriso unsquashfs mksquashfs magick zip python3; do
  command -v "$tool" >/dev/null 2>&1 || { echo "::error::Required tool missing: $tool"; exit 1; }
done

mkdir -p "$WORK_ROOT" "$(dirname "$OUTPUT_ISO")"
IMAGE="$WORK_ROOT/earthos-work.sparseimage"
MOUNT="$WORK_ROOT/mnt"
mkdir -p "$MOUNT"
if [[ ! -f "$IMAGE" ]]; then
  echo "Creating a 24 GB sparse, case-sensitive APFS work volume."
  hdiutil create -size 24g -type SPARSE -fs 'Case-sensitive APFS' -volname EARTHOS_WORK "$IMAGE"
fi
echo "Attaching case-sensitive work volume at $MOUNT."
hdiutil attach -nobrowse -mountpoint "$MOUNT" "$IMAGE"
df -h "$MOUNT"
cleanup() { hdiutil detach -quiet "$MOUNT" || true; }
trap cleanup EXIT

ISO_TREE="$MOUNT/iso"
SYSTEM_ROOT="$MOUNT/system"
mkdir -p "$ISO_TREE"
xorriso -osirrox on -indev "$BASE_ISO" -extract / "$ISO_TREE"

SYSTEM_IMAGE="$(find "$ISO_TREE" -type f -iname 'system.sfs' -print -quit)"
if [[ -z "$SYSTEM_IMAGE" ]]; then
  echo "::error::Android-x86 base ISO has no system.sfs; refusing to produce a misleading image."
  exit 1
fi
SYSTEM_REL="${SYSTEM_IMAGE#"$ISO_TREE"}"
SYSTEM_REL="/${SYSTEM_REL#/}"
echo "System image path in ISO: $SYSTEM_REL"

unsquashfs -no-progress -d "$SYSTEM_ROOT" "$SYSTEM_IMAGE"
# Android-x86 system.sfs normally stores the contents of /system at its root.
# Accept either layout so the repacker fails safely if the base differs.
if [[ -f "$SYSTEM_ROOT/build.prop" ]]; then
  SYSTEM_DIR="$SYSTEM_ROOT"
elif [[ -f "$SYSTEM_ROOT/system/build.prop" ]]; then
  SYSTEM_DIR="$SYSTEM_ROOT/system"
else
  echo "::error::Could not locate build.prop at the root of the extracted system image."
  find "$SYSTEM_ROOT" -maxdepth 3 -type f -name build.prop -print || true
  exit 1
fi
mkdir -p "$SYSTEM_DIR/media/earthos" "$SYSTEM_DIR/etc/earthos"

magick -background none "$REPO_ROOT/earthos/branding/earthos-wallpaper.svg" -resize 1920x1080! "$SYSTEM_DIR/media/earthos/EarthOS-Beta-01-wallpaper.png"
magick -background none "$REPO_ROOT/earthos/branding/boot-screen.svg" -resize 720x1280! "$MOUNT/boot-frame.png"

mkdir -p "$MOUNT/boot/part0"
cp "$MOUNT/boot-frame.png" "$MOUNT/boot/part0/00000.png"
printf '720 1280 30\np 1 0 part0\n' > "$MOUNT/boot/desc.txt"
(cd "$MOUNT/boot" && zip -q -r "$SYSTEM_DIR/media/bootanimation.zip" desc.txt part0)

BUILD_PROP="$SYSTEM_DIR/build.prop"
if [[ ! -f "$BUILD_PROP" ]]; then
  echo "::error::Could not find /system/build.prop in extracted Android-x86 image."
  exit 1
fi
python3 - "$BUILD_PROP" <<'PY'
import sys
path=sys.argv[1]
updates={
 "ro.product.brand":"EarthOS",
 "ro.product.manufacturer":"EarthOS Project",
 "ro.product.model":"EarthOS x86_64",
 "ro.product.name":"earthos_x86_64",
 "ro.build.display.id":"EarthOS Beta 01",
 "ro.earthos.version":"Beta 01",
 "ro.earthos.base":"Android-x86 9.0-r2",
 "ro.earthos.arch":"x86_64",
}
lines=open(path,encoding="utf-8").read().splitlines()
for key,value in updates.items():
    prefix=key+"="
    lines=[line for line in lines if not line.startswith(prefix)]
    lines.append(prefix+value)
with open(path,"w",encoding="utf-8",newline="\n") as f:
    f.write("\n".join(lines)+"\n")
PY

cat > "$SYSTEM_DIR/etc/earthos/release.json" <<'JSON'
{
  "name": "EarthOS",
  "version": "1.0.0-beta.1",
  "display_name": "EarthOS Beta 01",
  "android_base": "Android-x86 9.0-r2",
  "android_api": 28,
  "architecture": "x86_64",
  "ui_policy": "Stock Android 9 interface; no Experience UI applications or theme assets",
  "wallpaper": "/system/media/earthos/EarthOS-Beta-01-wallpaper.png"
}
JSON

# Keep the Android-x86 stock Launcher3, setup flow, and stock applications.
# Do not inject Experience UI APKs or replace stock framework resources.
STOCK_LAUNCHER="$(find "$SYSTEM_DIR" -type f -iname '*launcher*.apk' -print -quit || true)"
STOCK_SETUP="$(find "$SYSTEM_DIR" -type f \( -iname '*setupwizard*.apk' -o -iname '*provision*.apk' \) -print -quit || true)"
if [[ -z "$STOCK_LAUNCHER" ]]; then
  echo "::warning::No stock launcher APK was found by filename; review the system app inventory."
fi
if [[ -z "$STOCK_SETUP" ]]; then
  echo "::warning::No stock setup/provisioning APK was found by filename; Android-x86 may use its own first-boot flow."
fi

# Change only visible boot-menu labels, never kernel arguments or boot paths.
python3 - "$ISO_TREE" <<'PY'
import pathlib,re,sys
root=pathlib.Path(sys.argv[1])
for p in root.rglob("*"):
    if not p.is_file() or p.suffix.lower() not in {".cfg",".lst",".conf"}:
        continue
    try: text=p.read_text(encoding="utf-8")
    except Exception: continue
    out=[]
    for line in text.splitlines():
        if re.match(r"\s*menuentry\s+",line,re.I):
            line=re.sub(r"(['\"]).*?\1", "'EarthOS Beta 01 (Android 9.0)'", line, count=1)
        elif re.match(r"\s*MENU LABEL\s+",line,re.I):
            line=re.sub(r"(?i)(MENU LABEL\s+).*",r"\1EarthOS Beta 01",line)
        elif re.match(r"\s*title\s+.*Android-x86",line,re.I):
            line=re.sub(r"(?i)Android-x86.*","EarthOS Beta 01",line)
        out.append(line)
    p.write_text("\n".join(out)+"\n",encoding="utf-8")
PY

NEW_SYSTEM="$MOUNT/system-earthos.sfs"
mksquashfs "$SYSTEM_ROOT" "$NEW_SYSTEM" -comp gzip -b 131072 -noappend -all-root -no-xattrs
cp "$NEW_SYSTEM" "$SYSTEM_IMAGE"

# Rebuild by updating the extracted ISO tree while replaying its original boot
# catalogue. This retains the upstream BIOS/UEFI boot layout.
xorriso -indev "$BASE_ISO" -outdev "$OUTPUT_ISO" -boot_image any replay -update_r "$ISO_TREE" / -commit
test -s "$OUTPUT_ISO"
xorriso -indev "$OUTPUT_ISO" -report_el_torito plain > "$WORK_ROOT/rebuilt-boot-catalog.txt" 2>&1
file "$OUTPUT_ISO"
shasum -a 256 "$OUTPUT_ISO" | tee "$OUTPUT_ISO.sha256"
echo "EarthOS ISO created: $OUTPUT_ISO"
