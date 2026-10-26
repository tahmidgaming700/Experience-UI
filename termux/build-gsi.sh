#!/data/data/com.termux/files/usr/bin/bash
# Experience UI experimental GSI repack for Termux.
# This repacks an existing Android 12 ARM64 ext4 GSI; it does NOT build Android from source.
# No emulator test is attempted because Android Emulator is not supported as a normal Termux task.

set -Eeuo pipefail

REPO_URL="https://github.com/tahmidgaming700/Experience-UI.git"
BASE_URL="https://sourceforge.net/projects/andyyan-gsi/files/lineage-19.x/lineage-19.1-20250606-UNOFFICIAL-arm64_bgN-signed.img.gz/download"
BASE_FILE="lineage-19.1-20250606-UNOFFICIAL-arm64_bgN-signed.img.gz"
BUILD_TOOLS_VERSION="35.0.0"
ROOT_DIR="${EXPERIENCE_UI_DIR:-$HOME/Experience-UI}"
DIST="$ROOT_DIR/dist"

log() { printf '\n[Experience UI] %s\n' "$*"; }
die() { printf '\n[ERROR] %s\n' "$*" >&2; exit 1; }
have() { command -v "$1" >/dev/null 2>&1; }

log "Termux GSI repack — Android 12 / ARM64 (experimental)"
log "This needs substantial free storage and RAM. Keep the device plugged in."
log "It will not flash your device."

if ! have pkg; then die "Run this script inside Termux."; fi

if ! have git || ! have gradle || ! have java || ! have python || ! have pip || ! have wget || ! have gzip || ! have file || ! have sha256sum || ! have debugfs || ! have e2fsck || ! have resize2fs || ! have truncate || ! have unzip; then
  log "Installing Termux packages needed for the build/repack."
  pkg update
  pkg install -y git openjdk-17 gradle python python-pip wget gzip file e2fsprogs coreutils findutils unzip
fi

for cmd in git gradle java python wget gzip file sha256sum debugfs e2fsck resize2fs truncate unzip; do
  have "$cmd" || die "Missing required command '$cmd'. Install it with: pkg install $cmd"
done

if [[ ! -d "$ROOT_DIR/.git" ]]; then
  log "Cloning Experience UI repository into $ROOT_DIR"
  git clone "$REPO_URL" "$ROOT_DIR"
else
  log "Updating existing repository"
  git -C "$ROOT_DIR" pull --ff-only
fi

cd "$ROOT_DIR"
mkdir -p "$DIST/apks" "$DIST/bootanimation" "$DIST/metadata" "$DIST/image"

# The workflow uses Android SDK Build Tools 35.0.0 for aapt and apksigner.
# Termux does not have a universally supported Google sdkmanager setup, so require an existing SDK.
if [[ -z "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  die "Set ANDROID_HOME to a usable Android SDK containing platforms;android-35 and build-tools;35.0.0. Termux cannot reliably install Google's Linux SDK tools on every Android CPU."
fi
SDK_ROOT="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
AAPT="$SDK_ROOT/build-tools/$BUILD_TOOLS_VERSION/aapt"
APKSIGNER="$SDK_ROOT/build-tools/$BUILD_TOOLS_VERSION/apksigner"
[[ -x "$AAPT" ]] || die "Android aapt not found/executable: $AAPT"
[[ -x "$APKSIGNER" ]] || die "Android apksigner not found/executable: $APKSIGNER"
export ANDROID_HOME="$SDK_ROOT" ANDROID_SDK_ROOT="$SDK_ROOT"

log "Building all app modules"
gradle --no-daemon --stacktrace clean assembleDebug
modules=(launcher settings setupwizard dialer messages contacts notes clock calendar calculator files gallery camera converter ota-updater)
for module in "${modules[@]}"; do
  apk="$module/build/outputs/apk/debug/$module-debug.apk"
  [[ -s "$apk" ]] || die "APK missing or empty: $apk"
  cp "$apk" "$DIST/apks/Experience-$module.apk"
done

log "Validating APK archives, metadata and signatures"
for apk in "$DIST"/apks/*.apk; do
  "$AAPT" dump badging "$apk" >/dev/null
  "$APKSIGNER" verify --verbose "$apk"
  unzip -t "$apk" >/dev/null
done

log "Generating boot animation"
python -m pip install --user --disable-pip-version-check Pillow
python bootanimation/generate_bootanimation.py
[[ -s bootanimation/bootanimation.zip ]] || die "Boot animation ZIP was not generated."
[[ -s bootanimation/experience-boot-preview.gif ]] || die "Boot preview GIF was not generated."
unzip -t bootanimation/bootanimation.zip
cp bootanimation/bootanimation.zip bootanimation/experience-boot-preview.gif "$DIST/bootanimation/"

log "Downloading Android 12 ARM64 base GSI (large download)"
cd "$ROOT_DIR"
wget --https-only --max-redirect=20 --tries=4 --retry-connrefused --timeout=90 -O base.img.gz "$BASE_URL"
gzip -t base.img.gz
gzip -dc base.img.gz > base.img
[[ -s base.img ]] || die "Base image is empty."
file base.img | tee "$DIST/metadata/base-image-file.txt"
sha256sum base.img | tee "$DIST/metadata/base-image.sha256"
printf '%s\n' "$BASE_URL" > "$DIST/metadata/base-image-url.txt"
printf '%s\n' "$BASE_FILE" > "$DIST/metadata/base-image-filename.txt"

IMAGE_TYPE="$(file -b base.img)"
log "Base image type: $IMAGE_TYPE"
if grep -qi 'Android sparse' <<<"$IMAGE_TYPE"; then
  have simg2img || die "simg2img is required for sparse images. Install an Android sparse image utility compatible with Termux, or use a raw ext4 base image."
  simg2img base.img system.raw.img
else
  cp base.img system.raw.img
fi

if ! dumpe2fs -h system.raw.img > "$DIST/metadata/ext4-header.txt" 2>"$DIST/metadata/ext4-header-error.txt"; then
  die "Base image is not a directly writable ext4 filesystem. Refusing unsafe conversion."
fi
e2fsck -fn system.raw.img
truncate -s +512M system.raw.img
e2fsck -f -y system.raw.img
resize2fs system.raw.img
e2fsck -fn system.raw.img

path_type() {
  debugfs -R "stat $1" system.raw.img 2>&1 | sed -n 's/^[[:space:]]*Type: \([^ ]*\).*/\1/p' | head -n 1
}
write_image() { debugfs -w -R "$1" system.raw.img; }
ensure_dir() {
  local path="$1" type
  type="$(path_type "$path" || true)"
  if [[ "$type" != directory ]]; then write_image "mkdir $path"; fi
  [[ "$(path_type "$path" || true)" == directory ]] || die "Could not create directory in image: $path"
}

if [[ "$(path_type /system/app || true)" == directory ]]; then
  SYSTEM_ROOT=/system
elif [[ "$(path_type /app || true)" == directory ]]; then
  SYSTEM_ROOT=""
else
  debugfs -R 'ls -l /' system.raw.img || true
  die "Cannot locate Android app directory at /app or /system/app in this base image."
fi
for path in "$SYSTEM_ROOT/app" "$SYSTEM_ROOT/media" "$SYSTEM_ROOT/etc"; do
  [[ "$(path_type "$path" || true)" == directory ]] || die "Required directory missing in image: $path"
done

log "Injecting APKs and boot animation"
for apk in "$DIST"/apks/Experience-*.apk; do
  module="$(basename "$apk" .apk | sed 's/^Experience-//')"
  case "$module" in
    launcher) appdir=ExperienceLauncher ;;
    settings) appdir=ExperienceSettings ;;
    setupwizard) appdir=ExperienceSetupWizard ;;
    dialer) appdir=ExperienceDialer ;;
    messages) appdir=ExperienceMessages ;;
    contacts) appdir=ExperienceContacts ;;
    notes) appdir=ExperienceNotes ;;
    clock) appdir=ExperienceClock ;;
    calendar) appdir=ExperienceCalendar ;;
    calculator) appdir=ExperienceCalculator ;;
    files) appdir=ExperienceFiles ;;
    gallery) appdir=ExperienceGallery ;;
    camera) appdir=ExperienceCamera ;;
    converter) appdir=ExperienceConverter ;;
    ota-updater) appdir=ExperienceUpdater ;;
    *) die "Unexpected APK module: $module" ;;
  esac
  ensure_dir "$SYSTEM_ROOT/app/$appdir"
  write_image "write $apk $SYSTEM_ROOT/app/$appdir/$appdir.apk"
  write_image "set_inode_field $SYSTEM_ROOT/app/$appdir/$appdir.apk mode 0100644"
  debugfs -R "stat $SYSTEM_ROOT/app/$appdir/$appdir.apk" system.raw.img 2>&1 | grep -q 'Type: regular' || die "APK integration failed: $appdir"
done
write_image "write $DIST/bootanimation/bootanimation.zip $SYSTEM_ROOT/media/bootanimation.zip"
write_image "set_inode_field $SYSTEM_ROOT/media/bootanimation.zip mode 0100644"
ensure_dir "$SYSTEM_ROOT/etc/experience-ui"

cat > "$DIST/metadata/BUILD_INFO.txt" <<'INFO'
Experience UI — experimental Android 12 GSI repack
Base: Andy Yan LineageOS 19.1 ARM64 GSI
Included: Experience UI APKs and generated boot animation
OTA installation: not enabled
ARM64 GSI boot: not verified by this script
Huawei Y9 2019 JKM-LX2 compatibility: unverified
Galaxy Tab S SM-T805 compatibility: unverified
Do not flash until Treble/VNDK, vendor, bootloader, partition size and restore method are independently verified.
INFO
write_image "write $DIST/metadata/BUILD_INFO.txt $SYSTEM_ROOT/etc/experience-ui/BUILD_INFO.txt"
write_image "set_inode_field $SYSTEM_ROOT/etc/experience-ui/BUILD_INFO.txt mode 0100644"

log "Verifying injected files and filesystem"
e2fsck -fn system.raw.img
debugfs -R "ls -l $SYSTEM_ROOT/app" system.raw.img | tee "$DIST/metadata/integrated-apps.txt"
debugfs -R "stat $SYSTEM_ROOT/media/bootanimation.zip" system.raw.img > "$DIST/metadata/integrated-bootanimation.txt"
for appdir in ExperienceLauncher ExperienceSettings ExperienceSetupWizard ExperienceDialer ExperienceMessages ExperienceContacts ExperienceNotes ExperienceClock ExperienceCalendar ExperienceCalculator ExperienceFiles ExperienceGallery ExperienceCamera ExperienceConverter ExperienceUpdater; do
  debugfs -R "stat $SYSTEM_ROOT/app/$appdir/$appdir.apk" system.raw.img 2>&1 | grep -q 'Type: regular' || die "Missing integrated APK: $appdir"
done

log "Packaging experimental image"
cp --reflink=auto system.raw.img "$DIST/image/ExperienceUI-Android12-arm64-experimental.img" 2>/dev/null || cp system.raw.img "$DIST/image/ExperienceUI-Android12-arm64-experimental.img"
file "$DIST/image/ExperienceUI-Android12-arm64-experimental.img" | tee "$DIST/metadata/output-image-file.txt"
sha256sum "$DIST/image/ExperienceUI-Android12-arm64-experimental.img" | tee "$DIST/metadata/ExperienceUI-image.sha256"
cat > "$DIST/metadata/TEST-REPORT.txt" <<'REPORT'
App build and APK archive/signature/metadata checks: completed if script reached this point.
Boot animation archive generation/check: completed if script reached this point.
Ext4 filesystem checks and APK/boot animation injection: completed if script reached this point.
Android emulator smoke test: NOT RUN (not provided by this Termux script).
ARM64 GSI boot: NOT VERIFIED.
Huawei Y9 JKM-LX2 hardware boot: NOT TESTED.
Samsung Galaxy Tab S SM-T805 hardware boot: NOT TESTED.
OTA updater: metadata checker only; no OTA install path is enabled.
REPORT
cat > "$DIST/metadata/BUILD-STATUS.txt" <<'STATUS'
Experimental prebuilt GSI repack; not a full Android source build.
Base: Andy Yan LineageOS 19.1 ARM64.
GSI boot: not verified.
Device compatibility: unconfirmed.
Do not flash without independent compatibility and partition-size verification.
STATUS

log "DONE. Output files:"
printf '  GSI image: %s\n  APKs: %s\n  Boot animation: %s\n  Reports: %s\n' \
  "$DIST/image/ExperienceUI-Android12-arm64-experimental.img" "$DIST/apks" "$DIST/bootanimation" "$DIST/metadata"
log "This image is experimental and untested on physical devices. Do NOT flash it as a daily-driver ROM."
