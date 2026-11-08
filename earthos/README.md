# EarthOS Beta 01

**A Migrational Shift from Experience UI.**

EarthOS is the x86_64 Android operating-system project built from the Experience UI app ecosystem. It is not a GSI and is not an Android emulator.

## First base

- ISO: Android-x86 9.0-r2
- File: `android-x86_64-9.0-r2.iso`
- Target architecture: x86_64
- Android version: 9.0 / API level 28
- CI runner: GitHub-hosted `macos-26-intel` (x86_64)

## Beta 01 workflow

The first workflow is deliberately an **audit, not yet an ISO repack**. It downloads the selected ISO, checks that the runner is x86_64, extracts and inventories the ISO, identifies the kernel/initrd/system image, builds the existing Experience UI APK modules, and compares each APK's minimum SDK with Android 9.0.

Run it from GitHub:

1. Open the repository's **Actions** tab.
2. Select **EarthOS Beta 01 — Android-x86 9.0 audit**.
3. Select the `earthos/beta-01-audit` branch.
4. Choose **Run workflow**.
5. Download the `earthos-beta-01-base-audit` artifact when it finishes.

No terminal, WSL, or local build environment is required.

## Compatibility gate — important

The current Experience UI app modules declare `minSdk = 28` (Android 9), while Android-x86 7.1 is API 28. Those APKs cannot be installed on Android 9.0 unchanged. The audit workflow reports this instead of silently placing incompatible APKs into the ISO.

Before producing a bootable EarthOS image, we must choose and validate one of these paths:

- Port the Experience UI apps and their dependencies back to API 28, module by module; or
- Select a newer Android-x86 base that meets the apps' minimum API requirements.

Do not lower `minSdk` mechanically: API calls, AndroidX/Compose dependency requirements, manifest features and runtime behaviour must all be checked.

## Build stages

1. **Base audit:** verify ISO format, boot files, system image and app compatibility.
2. **EarthOS artwork:** create a coherent logo, wallpaper family, installer visuals and boot animation.
3. **System integration:** add only apps and resources proven compatible with the selected Android base.
4. **ISO rebuild:** preserve the original boot configuration and verify the resulting ISO structure and checksum.
5. **Release:** publish a clearly labelled experimental ISO with build report and known limitations.

A successful CI run or ISO repack does not prove that the image boots on every x86 computer. Test in a virtual machine and on suitable x86_64 hardware before calling it a usable release.
