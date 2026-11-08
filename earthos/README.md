# EarthOS Beta 01

**A Migrational Shift from Experience UI.**

EarthOS is an independent x86_64 operating-system image based on Android-x86 9.0-r2. It is not a GSI and is not an Android emulator.

## Beta 01 identity

- Release: EarthOS Beta 01
- Version: 1.0.0-beta.1
- Base: Android-x86 9.0-r2
- Android API: 28
- Architecture: x86_64
- CI runner: GitHub-hosted macOS 26 Intel

## Visual policy for Beta 01

Beta 01 deliberately uses the **stock Android 9 interface**. It does not install the Experience UI launcher, setup wizard, apps, theme, or design language. The upstream Android-x86 stock launcher, setup/first-boot flow, and stock applications are preserved. EarthOS branding is independent: boot-menu label, a restrained EarthOS boot animation, system identity properties, release metadata, and a wallpaper asset.

The Experience UI app modules are still built from source in CI as a separate source-build check, but they are not injected into this ISO. The Experience UI visual direction is reserved for the later Android 10 milestone.

## Build and release

The workflow `EarthOS Beta 01 — build and release`:

1. Downloads the official Android-x86 9.0-r2 ISO.
2. Builds the repository's Android app modules from source as a separate validation step.
3. Extracts the base ISO and its SquashFS system image.
4. Adds EarthOS branding and release identity while preserving stock Android applications and setup.
5. Rebuilds the ISO while replaying the upstream BIOS/UEFI boot catalogue.
6. Generates a SHA-256 checksum and machine-readable OTA release manifest.
7. Publishes the ISO and manifest as a prerelease asset when the release workflow completes successfully.

A successful ISO build is not the same as a successful hardware boot test. Treat Beta 01 as experimental until it has been tested in a VM and on real x86_64 hardware.

See [BUILDING.md](BUILDING.md) for source-build instructions and the OTA manifest contract.
