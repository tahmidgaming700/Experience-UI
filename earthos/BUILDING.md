# Building EarthOS Beta 01

## Base and design policy

- Base: Android-x86 9.0-r2 (Android API 28), x86_64.
- Beta 01 intentionally keeps the stock Android-x86 interface.
- The Experience UI launcher, setup wizard, themes, and application suite are **not** installed in this release.
- The stock Android-x86 launcher, first-boot flow, and stock applications remain in place.
- EarthOS branding is limited to the boot menu, boot animation, system properties, release metadata, and an included wallpaper.

## Build the Android application source modules

The repository contains the separate Android application modules. From the repository root, use the GitHub Actions workflow or a local Android SDK/Java 17/Gradle 8.9 environment:

```bash
gradle assembleDebug --no-daemon
```

This compiles the source modules from scratch. These Experience UI applications are intentionally **not** bundled into EarthOS Beta 01 because Beta 01 must not use the Experience UI visual design. The app suite can be reconsidered for the Android 10 milestone.

## Build the EarthOS ISO

The supported reproducible build runs in GitHub Actions on a GitHub-hosted macOS 26 Intel runner. It downloads the official Android-x86 9.0-r2 ISO, builds the app source modules as a separate validation step, extracts the SquashFS system image, applies EarthOS identity assets and system properties, then rebuilds the ISO while replaying the original boot catalogue.

The build requires Homebrew packages `xorriso`, `squashfs`, and `imagemagick`, plus Java 17, Gradle 8.9, and the Android SDK.

The build does not claim hardware compatibility until the resulting ISO has been boot-tested on representative x86_64 machines.

## OTA manifest

`earthos/ota/manifest.json` is the stable bootstrap pointer. The release workflow generates `ota-manifest.json` with the actual ISO URL, size, and SHA-256 checksum and attaches it to the GitHub Release. The package is a full installation ISO, not a delta OTA.
