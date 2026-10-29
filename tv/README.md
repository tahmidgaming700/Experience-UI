# Experience UI TV — x86_64

This is a separate PC/mini-PC/TV target. It is **not** the ARM64 Huawei Y9 GSI.

## Base image

The experimental workflow uses the upstream [LineageOS TV x86_64 Android 14 ISO](https://sourceforge.net/projects/lineageos-tv-x86/files/lineage-21.0/x86_64_tv/), specifically:

`lineage-21.0-20260331-UNOFFICIAL-x86_64_tv-signed.iso`

Upstream notes say these are generic x86_64 Android TV builds; the standard `x86_64_tv` variant is distinct from the lower-memory `x86_64_tv_go` variant. Check the upstream project for current releases and hardware notes.

## Build

1. Open the [Experience UI TV x86_64 workflow](../.github/workflows/build-tv-x86.yml) in GitHub Actions.
2. Choose **Run workflow** on branch `rebuild/functional-system-apps`, or push a change under `tv/` or one of the included app modules.
3. Download the `experience-ui-tv-x86_64-experimental` artifact after a successful run.
4. Read `metadata/TEST-REPORT.txt` and `metadata/ExperienceUI-TV-x86_64.sha256` before testing.

The workflow builds the current app suite, checks the upstream ISO, injects the APKs into a directly writable ext4 system image, and asks xorriso to replay the original boot configuration when rebuilding the ISO. It deliberately fails if the downloaded ISO layout or filesystem differs from the assumptions; do not bypass that check without inspecting the image format.

## Important limitations

- This is an experimental image repack, **not a full Android source build**.
- The launcher now selects a dedicated 10-foot TV home screen on devices reporting the Android Leanback feature. It includes large focus-highlighted tiles, D-pad/click navigation, a remote-keyboard app search, a featured row and a Settings shortcut.
- All app modules declare touchscreen hardware as optional, so they are not filtered out solely for lacking a touchscreen. This is a compatibility declaration, not proof that every screen is fully remote-optimized.
- Existing app screens remain early foundations. Individual phone-oriented workflows may still need larger layouts, explicit focus order and remote-specific interaction testing.
- No physical PC/TV boot, HDMI audio, Wi-Fi/Bluetooth, DRM, streaming playback, sleep/wake or remote-control tests are claimed.
- Adding an APK under `/system/app` does not automatically grant every privileged permission, and does not guarantee that the launcher becomes the default HOME app.
- A repack can invalidate upstream signing/verification assumptions. Keep the original ISO, test on a spare PC or virtual machine first, and do not overwrite a working installation.
- The ARM64 Android 11 GSI workflow remains separate and must not be used on x86_64 PCs.

## Upstream source

- [LineageOS TV x86 project](https://github.com/LineageOS-TV-x86)
- [Upstream ISO directory](https://sourceforge.net/projects/lineageos-tv-x86/files/lineage-21.0/x86_64_tv/)
