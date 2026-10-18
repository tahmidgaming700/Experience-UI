#!/usr/bin/env bash
set -euo pipefail

# Run from the root of a matching LineageOS source checkout.
# Pass the directory where you extracted the GitHub Actions artifact.
ARTIFACT_DIR="${1:-}"
if [[ -z "$ARTIFACT_DIR" || ! -d "$ARTIFACT_DIR" ]]; then
  echo "Usage: $0 /path/to/extracted/experience-ui-app-suite-and-boot-animation"
  exit 2
fi

mkdir -p vendor/experience/prebuilt vendor/experience/media
for apk in "$ARTIFACT_DIR"/apks/experience-*-debug.apk; do
  [[ -f "$apk" ]] || { echo "No APKs found in $ARTIFACT_DIR/apks"; exit 1; }
  cp -f "$apk" vendor/experience/prebuilt/
done

animation="$ARTIFACT_DIR/bootanimation/bootanimation.zip"
[[ -f "$animation" ]] || { echo "Missing bootanimation.zip at $animation"; exit 1; }
cp -f "$animation" vendor/experience/media/bootanimation.zip
cp -f "$(dirname "$0")/experience_apps.mk" vendor/experience/experience_apps.mk

echo "Prepared $(find vendor/experience/prebuilt -maxdepth 1 -name '*.apk' | wc -l) APKs."
echo "Next: add '$(call inherit-product, vendor/experience/experience_apps.mk)' to the device product makefile for the exact target."
echo "No ROM was built or flashed by this script."
