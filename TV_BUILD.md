# Experience UI TV x86_64

Experience UI TV is a separate experimental target based on the unofficial LineageOS TV Android 14 x86_64 ISO dated 2026-03-31. It is not the ARM64 phone GSI and cannot be flashed to the Huawei Y9 or other ARM64 devices.

## TV app set

The TV image integrates these Experience UI modules:

- Launcher (D-pad navigation, visible focus states, app grid and search)
- Settings (large-screen layout and remote-friendly focus feedback)
- Setup Wizard
- Notes, Clock, Calendar, Calculator
- Files, Gallery, Converter, OTA Updater

The TV build does not compile or integrate the Experience UI Dialer, Contacts, Messages or Camera modules. The TV launcher also hides common phone-only apps from its app grid. Upstream system packages are not blindly deleted because they can be dependencies of the base OS.

## Build

Open **Actions → Build Experience UI TV x86_64 ISO → Run workflow** or push changes to the TV-relevant modules on `rebuild/functional-system-apps`.

The workflow:
1. Compiles only the selected TV app modules and validates APKs.
2. Downloads and records the upstream ISO checksum.
3. Extracts the EROFS system image with `erofs-utils` (never with ext4-only `debugfs`/ `e2fsck`).
4. Adds the TV app APKs and Experience UI build information.
5. Rebuilds EROFS, validates the rebuilt image, and asks xorriso to replay the original boot records.
6. Uploads the experimental ISO, APKs and validation metadata as a 14-day workflow artifact.

## Important limitations

- A successful CI build does not prove the ISO boots on a physical PC or TV box.
- Hardware graphics, audio, Wi-Fi, Bluetooth, remote input, SELinux policy, DRM and streaming applications need real-device testing.
- The workflow records and uses Android file contexts when available; inspect the validation metadata and SELinux labels before treating an image as release-ready.
- Keep the original ISO and test the output in a virtual machine or spare PC first. Do not overwrite your only working installation.
- The OTA updater UI does not provide a signed OTA installation path.
