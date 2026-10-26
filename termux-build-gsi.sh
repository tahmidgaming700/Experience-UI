#!/data/data/com.termux/files/usr/bin/bash
# Experience UI experimental GSI repack for Termux.
# Mirrors .github/workflows/build-gsi.yml where Termux can support it.
# This is a repack of a prebuilt GSI, not a full Android source build.
# Android Emulator smoke test is omitted because ordinary Termux cannot run the x86_64 emulator.

set -Eeuo pipefail
REPO_URL="https://github.com/tahmidgaming700/Experience-UI.git"
BASE_URL="https://sourceforge.net/projects/andyyan-gsi/files/lineage-19.x/lineage-19.1-20250606-UNOFFICIAL-arm64_bgN-signed.img.gz/download"
BASE_FILE="lineage-19.1-20250606-UNOFFICIAL-arm64_bgN-signed.img.gz"
WORKDIR="${EXPERIENCE_UI_WORKDIR:-$HOME/Experience-UI}"
BUILD_TOOLS_VERSION="35.0.0"

die() { printf '\\n[ERROR] %s\\n' "$*" >&2; exit 1; }
need_cmd() { command -v "$1" >/dev/null 2>&1 || die "Required command not found: $1"; }

[[ -n "${PREFIX:-}" ]] || die "Run this script inside Termux."
printf '%s\\n' "=== Experience UI Termux GSI builder ==="
printf '%s\\n' "This script repacks a downloaded system image. It does not flash your device."

pkg update -y
pkg install -y git wget gzip file python python-pip openjdk-17 gradle e2fsprogs qemu-utils coreutils findutils grep sed unzip zip
python -m pip install --user --upgrade Pillow || python -m pip install --upgrade Pillow

if [[ -d "$WORKDIR/.git" ]]; then git -C "$WORKDIR" pull --ff-only; else git clone "$REPO_URL" "$WORKDIR"; fi
cd "$WORKDIR"
export JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")"
export PATH="$JAVA_HOME/bin:$PATH"
for c in java gradle debugfs dumpe2fs e2fsck resize2fs qemu-img; do need_cmd "$c"; done

mkdir -p dist/apks dist/bootanimation dist/metadata dist/image
printf '\\n[1/7] Building app modules...\\n'
gradle --no-daemon --stacktrace clean assembleDebug
modules=(launcher settings setupwizard dialer messages contacts notes clock calendar calculator files gallery camera converter ota-updater)
for module in "${modules[@]}"; do
  apk="$module/build/outputs/apk/debug/$module-debug.apk"
  [[ -s "$apk" ]] || die "APK missing: $apk. Check whether Android SDK/Gradle is configured."
  cp "$apk" "dist/apks/Experience-$module.apk"
done

printf '\\n[2/7] Validating APK ZIPs...\\n'
AAPT="${ANDROID_HOME:-$HOME/Android/Sdk}/build-tools/$BUILD_TOOLS_VERSION/aapt"
APKSIGNER="${ANDROID_HOME:-$HOME/Android/Sdk}/build-tools/$BUILD_TOOLS_VERSION/apksigner"
if [[ -x "$AAPT" && -x "$APKSIGNER" ]]; then
  for apk in dist/apks/*.apk; do "$AAPT" dump badging "$apk" >/dev/null; "$APKSIGNER" verify --verbose "$apk"; unzip -t "$apk" >/dev/null; done
else
  printf '%s\\n' "WARNING: Android SDK aapt/apksigner not found; signature/metadata checks skipped."
  for apk in dist/apks/*.apk; do unzip -t "$apk" >/dev/null; done
fi

printf '\\n[3/7] Generating boot animation...\\n'
python bootanimation/generate_bootanimation.py
[[ -s bootanimation/bootanimation.zip ]] || die "Boot animation ZIP was not generated."
[[ -s bootanimation/experience-boot-preview.gif ]] || die "Boot preview GIF was not generated."
unzip -t bootanimation/bootanimation.zip
cp bootanimation/bootanimation.zip bootanimation/experience-boot-preview.gif dist/bootanimation/

printf '\\n[4/7] Downloading Android 12 ARM64 base GSI...\\n'
[[ -s base.img.gz ]] || wget --https-only --max-redirect=20 --tries=4 --retry-connrefused --timeout=90 -O base.img.gz "$BASE_URL"
gzip -t base.img.gz
gzip -dc base.img.gz > base.img
[[ -s base.img ]] || die "Base image is empty."
file base.img | tee dist/metadata/base-image-file.txt
sha256sum base.img | tee dist/metadata/base-image.sha256
printf '%s\\n' "$BASE_URL" > dist/metadata/base-image-url.txt
printf '%s\\n' "$BASE_FILE" > dist/metadata/base-image-filename.txt

printf '\\n[5/7] Preparing filesystem...\\n'
IMAGE_TYPE="$(file -b base.img)"
if grep -qi 'Android sparse' <<<"$IMAGE_TYPE"; then
  need_cmd simg2img
  simg2img base.img system.raw.img
else cp base.img system.raw.img; fi
file system.raw.img | tee dist/metadata/working-image-file.txt
dumpe2fs -h system.raw.img >dist/metadata/ext4-header.txt 2>dist/metadata/ext4-header-error.txt || die "Image is not a directly writable ext4 filesystem; refusing unsafe conversion."
e2fsck -fn system.raw.img
truncate -s +512M system.raw.img
e2fsck -f -y system.raw.img
resize2fs system.raw.img
e2fsck -fn system.raw.img

path_type() { debugfs -R "stat $1" system.raw.img 2>&1 | sed -n 's/^[[:space:]]*Type: \\([^ ]*\\).*/\\1/p' | head -n 1; }
dir_exists() {
  local details
  details="$(debugfs -R "stat $1" system.raw.img 2>&1 || true)"
  grep -q 'Type: directory' <<<"$details"
}
write_image() { debugfs -w -R "$1" system.raw.img >/dev/null; }
ensure_dir() {
  local p="$1"
  dir_exists "$p" || write_image "mkdir $p"
  dir_exists "$p" || die "Could not create image directory: $p"
}

if dir_exists /system/app; then SYSTEM_ROOT=/system
elif dir_exists /app; then SYSTEM_ROOT=
else debugfs -R 'ls -l /' system.raw.img || true; debugfs -R 'ls -l /system' system.raw.img || true; die "Cannot detect Android system root (/app or /system/app)."; fi
echo "Detected system root: ${SYSTEM_ROOT:-/}"
for d in app etc; do dir_exists "$SYSTEM_ROOT/$d" || die "Required directory missing: $SYSTEM_ROOT/$d"; done
# Some valid GSIs omit /system/media; create it before adding bootanimation.
ensure_dir "$SYSTEM_ROOT/media"

printf '\\n[6/7] Integrating APKs and boot animation...\\n'
for apk in dist/apks/Experience-*.apk; do
  module="$(basename "$apk" .apk | sed 's/^Experience-//')"
  case "$module" in
    launcher) appdir=ExperienceLauncher;; settings) appdir=ExperienceSettings;; setupwizard) appdir=ExperienceSetupWizard;;
    dialer) appdir=ExperienceDialer;; messages) appdir=ExperienceMessages;; contacts) appdir=ExperienceContacts;;
    notes) appdir=ExperienceNotes;; clock) appdir=ExperienceClock;; calendar) appdir=ExperienceCalendar;;
    calculator) appdir=ExperienceCalculator;; files) appdir=ExperienceFiles;; gallery) appdir=ExperienceGallery;;
    camera) appdir=ExperienceCamera;; converter) appdir=ExperienceConverter;; ota-updater) appdir=ExperienceUpdater;;
    *) die "Unexpected APK module: $module";;
  esac
  ensure_dir "$SYSTEM_ROOT/app/$appdir"
  write_image "write $apk $SYSTEM_ROOT/app/$appdir/$appdir.apk"
  write_image "set_inode_field $SYSTEM_ROOT/app/$appdir/$appdir.apk mode 0100644"
  [[ "$(path_type "$SYSTEM_ROOT/app/$appdir/$appdir.apk" || true)" == regular ]] || die "APK injection failed: $appdir"
done
write_image "write dist/bootanimation/bootanimation.zip $SYSTEM_ROOT/media/bootanimation.zip"
write_image "set_inode_field $SYSTEM_ROOT/media/bootanimation.zip mode 0100644"
ensure_dir "$SYSTEM_ROOT/etc/experience-ui"
cat > dist/metadata/BUILD_INFO.txt <<'INFO'
Experience UI — experimental Android 12 GSI repack
Base: Andy Yan LineageOS 19.1 GApps ARM64
Included: Experience UI APKs and generated boot animation
OTA installation: not enabled
Architecture: ARM64
Huawei Y9 2019 JKM-LX2 compatibility: unverified
Not a device-specific ROM. Verify Treble/VNDK, vendor, bootloader, partition capacity, and a tested restore method before considering any flash.
INFO
write_image "write dist/metadata/BUILD_INFO.txt $SYSTEM_ROOT/etc/experience-ui/BUILD_INFO.txt"
write_image "set_inode_field $SYSTEM_ROOT/etc/experience-ui/BUILD_INFO.txt mode 0100644"
e2fsck -fn system.raw.img

printf '\\n[7/7] Verifying and packaging...\\n'
if dir_exists /system/app; then VERIFY_ROOT=/system; else VERIFY_ROOT=; fi
debugfs -R "ls -l $VERIFY_ROOT/app" system.raw.img | tee dist/metadata/integrated-apps.txt
for appdir in ExperienceLauncher ExperienceSettings ExperienceSetupWizard ExperienceDialer ExperienceMessages ExperienceContacts ExperienceNotes ExperienceClock ExperienceCalendar ExperienceCalculator ExperienceFiles ExperienceGallery ExperienceCamera ExperienceConverter ExperienceUpdater; do
  debugfs -R "stat $VERIFY_ROOT/app/$appdir/$appdir.apk" system.raw.img 2>&1 | grep -q 'Type: regular' || die "Missing integrated APK: $appdir"
done
debugfs -R "stat $VERIFY_ROOT/media/bootanimation.zip" system.raw.img >dist/metadata/integrated-bootanimation.txt
debugfs -R "stat $VERIFY_ROOT/etc/experience-ui/BUILD_INFO.txt" system.raw.img >dist/metadata/integrated-build-info.txt
e2fsck -fn system.raw.img
cp system.raw.img dist/image/ExperienceUI-Android12-arm64-experimental.img
file dist/image/ExperienceUI-Android12-arm64-experimental.img | tee dist/metadata/output-image-file.txt
qemu-img info dist/image/ExperienceUI-Android12-arm64-experimental.img | tee dist/metadata/qemu-image-info.txt
sha256sum dist/image/ExperienceUI-Android12-arm64-experimental.img | tee dist/metadata/ExperienceUI-image.sha256
cat > dist/metadata/TEST-REPORT.txt <<'REPORT'
APK ZIP checks: performed.
Boot animation generation/archive check: performed.
Filesystem checks and injection path checks: performed if Termux e2fsprogs supports the base image.
Android Emulator smoke test: NOT RUN by this Termux script.
ARM64 GSI virtual-machine boot: NOT VERIFIED.
Huawei Y9 JKM-LX2 hardware boot: NOT TESTED.
OTA install: not enabled.
REPORT
printf '\\nBuild/repack finished. Outputs:\\n%s/dist/image/ExperienceUI-Android12-arm64-experimental.img\\n%s/dist/apks/\\n%s/dist/bootanimation/bootanimation.zip\\n' "$WORKDIR" "$WORKDIR" "$WORKDIR"
printf '\\nDo not flash unless device compatibility, partition size, and a restore path are independently verified.\\n'
