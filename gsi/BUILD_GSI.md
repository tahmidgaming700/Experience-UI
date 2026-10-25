# Building an Experience UI LineageOS GSI

This is a source-build recipe, not a prebuilt image. The ROM source, build host and device compatibility must be verified independently.

## Source choice

Experience UI's first GSI build experiment uses the community Treble-Droid LineageOS GSI workflow maintained at [MisterZtr/LineageOS_gsi](https://github.com/MisterZtr/LineageOS_gsi) and its matching [treble_manifest](https://github.com/MisterZtr/treble_manifest). These are community projects, not official LineageOS releases. Read their current README, branch names, patches and licences before building. Keep the ROM branch and local manifest branch matched; do not mix instructions from different Android versions.

The current upstream instructions surfaced for that project use the `lineage-23.2` source branch. Treat that as a reproducible starting point, not a claim that it is the newest Android branch forever. If upstream has moved to a newer compatible branch, update both source and local-manifest branches together and record the change here.

## Host requirements

Use a supported 64-bit Linux build host, not Android/Termux or a Windows shell. Android ROM builds require substantial RAM, storage and a stable network connection. Plan for hundreds of gigabytes of free disk space and 16 GB RAM minimum (32 GB or more recommended). Use the requirements documented by the selected upstream source and LineageOS for the exact branch.

## Source preparation

Install the `repo` tool and the packages required by the chosen LineageOS branch first. Then, in a dedicated build directory, follow the upstream project README. The high-level sequence is:

```bash
mkdir -p ~/android/experience-lineage-gsi
cd ~/android/experience-lineage-gsi

# Initialise the LineageOS branch specified by the matching upstream GSI guide.
repo init -u https://github.com/LineageOS/android.git -b lineage-23.2 --git-lfs
mkdir -p .repo/local_manifests
git clone https://github.com/MisterZtr/treble_manifest.git \
  -b lineage-23.2 .repo/local_manifests
repo sync
```

Next, obtain the matching `LineageOS_gsi` patch set, inspect it, and apply it exactly as its upstream README instructs. Do not blindly apply a patch set intended for another branch. Generate the Treble product makefiles as documented upstream, then choose a target supported by that exact checkout.

## ARM64 variants

The target names and suffixes are branch-specific. Upstream GSI projects have used names such as:

- `lineage_arm64_bvN4-bp4a-userdebug`: ARM64, A/B-style system-as-root family, vanilla/no Google Apps, ext4-style variant in the cited upstream naming.
- `lineage_arm64_bgN4-bp4a-userdebug`: corresponding ARM64 variant with Google Apps included by that build configuration.

**Do not copy these lunch targets blindly.** Confirm the exact target names in the checked-out branch's generated product makefiles and README before running `lunch` or `breakfast`. Some upstream trees use different suffixes or filesystems. Build only a target that actually exists in the checkout.

Typical final build step, after upstream patches and product generation have succeeded:

```bash
source build/envsetup.sh
# Choose the exact product target verified in this checkout.
lunch <verified-lineage-arm64-gsi-target>
make systemimage -j"$(nproc)"
```

The image should appear under the output directory reported by the build system, commonly a `out/target/product/*` directory. Locate it rather than assuming a fixed product directory.

## What the suffixes mean

- `arm64`: 64-bit ARM userspace. It is **not** interchangeable with `arm` or `a64`.
- `b`: A/B or system-as-root family in these community GSI naming schemes; confirm the upstream branch's exact meaning.
- `v`: vanilla, without Google Apps.
- `g`: Google Apps included by the GSI build recipe.
- `N`: no included PHH superuser in the cited naming convention.
- `vndklite`: a special compatibility variant for some VNDKLite or writable-system cases; it is not a general “better” build.

“ARM64”, “A/B”, “vanilla” and “with Google Apps” describe different properties, not one single option. Select the architecture and partition layout from the device's read-only properties. Google Apps must match the Android branch and licensing/redistribution requirements; do not bundle or redistribute proprietary Google packages without permission.

## Integrating Experience UI

The boot animation and APK suite in this repository are not yet built into the GSI system image. Proper ROM integration requires product makefiles/overlays and package inclusion in the checked-out source tree, followed by a full build. Installing APKs after boot is not the same as making them privileged system components.

## Device safety gate

Both known targets remain research-only:

- Huawei Y9 2019 JKM-LX2: do not flash until bootloader access, Treble support, ABI, VNDK compatibility, partition layout and recovery route are established.
- Samsung Galaxy Tab S 10.5 SM-T805/chagalllte: do not assume a modern GSI is compatible with its legacy kernel/vendor stack.

Collect read-only properties first. Never flash a generic image based on CPU architecture alone. Verify checksums and preserve stock firmware before any experiment.

## Current status

No full GSI has been built or boot-tested by this repository yet. A successful Android APK workflow does not imply a ROM build succeeded.
