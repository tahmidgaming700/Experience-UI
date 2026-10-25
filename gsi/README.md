# Experience UI — GSI edition

This directory defines the **GSI customization layer** for Experience UI. It is not a prebuilt ROM and does not claim that any device can boot a Generic System Image.

## Architecture

- **Base:** a separately obtained, verified LineageOS-derived GSI image compatible with the target's Android version, CPU ABI, partition layout and Treble/VNDK requirements.
- **Experience UI layer:** product identity, overlays, boot animation, default-app selection and optional APK bundle.
- **Device compatibility:** tracked per device; no generic flashing script and no automatic repartitioning.
- **Pico AI:** optional standalone app integration only after its package ID, API and offline model licence are verified.

## Target status

| Device | Status | Notes |
|---|---|---|
| Huawei Y9 2019 (JKM-LX2, Kirin 710) | Research only | Bootloader unlock availability and Treble/partition compatibility are not confirmed. Do not flash. |
| Samsung Galaxy Tab S 10.5 (SM-T805, chagalllte) | Research only | Legacy device; a modern GSI may lack compatible kernel/vendor components. Do not flash. |

## Build guide\n\nFollow [`BUILD_GSI.md`](BUILD_GSI.md) for the source-build plan, ARM64 variant terminology, and build requirements. It explicitly distinguishes the community Treble-Droid GSI source from official LineageOS device builds.\n\n## GSI workflow

1. Identify exact model and firmware; back up user data.
2. Collect read-only device properties: `ro.treble.enabled`, `ro.product.cpu.abi`, `ro.build.version.release`, `ro.product.first_api_level`, and partition information.
3. Verify the base image source, signature/checksum, Android release, ABI, VNDK and A/B or A-only layout.
4. Build/test the Experience UI apps and overlays separately.
5. Integrate overlays in a source-based ROM build where possible. Do not blindly unpack/repack or re-sign a GSI.
6. Test on a spare device or emulator where possible; preserve stock firmware and a documented recovery route.

## Important limitation

A GSI is a generic **system image**, not a complete device-specific ROM source tree. It does not provide the device kernel, vendor implementation, boot image, recovery, verified-boot configuration or bootloader unlock. A custom OS cannot be declared bootable until the exact target has been validated and the image has been built and tested.

## No unsafe automation

This repository intentionally does not include commands that unlock the bootloader, erase partitions, disable verified boot, repartition storage or flash an unverified image. Never use a package intended for another device.