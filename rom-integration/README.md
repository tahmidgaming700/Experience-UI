# Experience UI source integration

This integration installs the Experience UI app suite as ordinary, non-privileged system apps in a LineageOS-derived Android source build. It also installs the Experience UI boot animation and adds separate `ro.experience.ui.*` identity properties. It deliberately does not spoof the upstream LineageOS build fingerprint, security patch level, signing identity, or official build number.

## What this does and does not do

- Adds the Experience launcher and companion APKs to the system image at compile time.
- Adds Experience UI build metadata while preserving the actual base ROM identity.
- Does **not** replace Android SystemUI, framework, SettingsProvider, permissions policy, device HALs, kernel, boot image, or recovery. The Experience Settings app is an app, not a replacement for all platform Settings.
- Debug-signed APKs are not privileged and should remain under `system/app`, not `priv-app`.

## Prepare the source tree

Build the repository's app suite and boot animation using the Experience UI app-suite workflow, then extract its artifact. The artifact contains `apks/Experience-*.apk` and `bootanimation/bootanimation.zip`.

From the root of a matching LineageOS GSI source checkout:

```bash
mkdir -p vendor/experience/prebuilt vendor/experience/media
cp /path/to/artifact/apks/Experience-*.apk vendor/experience/prebuilt/
cp /path/to/artifact/bootanimation/bootanimation.zip vendor/experience/media/
cp /path/to/Experience-UI/rom-integration/Android.mk vendor/experience/Android.mk
cp /path/to/Experience-UI/rom-integration/experience.mk vendor/experience/experience.mk
```

Add the following to the product makefile actually inherited by the chosen lunch target (verify its path in the checked-out source branch):

```make
include vendor/experience/experience.mk
```

The prebuilt modules are defined by `vendor/experience/Android.mk`. Keep the upstream stock launcher and core apps until each Experience app has been tested for all default-role contracts and platform permissions.

## A/B and system-as-root

The intended GSI build family is ARM64 A/B/system-as-root, using the branch's verified `lineage_arm64_bvN4-bp4a-userdebug` or `lineage_arm64_bgN4-bp4a-userdebug` lunch target. Target naming is upstream-specific; verify it exists before building. A GSI system image is not a complete device ROM and cannot supply a compatible kernel/vendor image for a legacy device by itself.

## Identity and safety

Only `ro.experience.ui.*` values are changed. The upstream `ro.build.fingerprint`, build ID, security patch level, and signing are preserved to avoid misrepresenting a community build as an official LineageOS release. Never flash until the exact target's Treble/VNDK, ABI, partition layout, bootloader, vendor and recovery compatibility are verified.
