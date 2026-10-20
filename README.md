# Experience UI

**A modular Android app suite for the Experience UI ROM project.** Each core experience is a separate installable APK, sharing a Material 3 design system. GitHub Actions builds the app suite and generates the Experience UI boot animation.

## Apps in this milestone

| APK / module | Current scope |
|---|---|
| Launcher | Searchable app grid; launcher role candidate; app launch integration is being expanded |
| Setup Wizard | Guided first-run steps, saved language/appearance preferences, links to protected Android setup |
| Phone / Dialer | Number pad and safe hand-off to the system dialler |
| Messages | Conversation UI foundation; SMS provider/default-SMS role integration remains future work |
| Contacts | Opens the device's contacts provider |
| Notes | Simple note saved locally on the device |
| Clock | Shows current time and opens system alarms/timer |
| Calendar | Opens calendar and hands off event creation to a calendar provider |
| Calculator | Basic two-number arithmetic |
| Settings | Shortcuts to Android network, display, security, apps and device information settings |
| Files | Android document picker |
| Gallery | Android photo/video picker |
| Camera | Opens the installed camera app |
| OS Converter | Device-specific ROM planning checklist; it does not flash firmware |

Shared UI lives in `:core-ui`. These are **early app foundations**, not yet a complete replacement for every Google/Samsung system application. Features requiring privileged system APIs, a complete dialler/SMS role implementation, camera hardware integration, calendar/contact databases, media indexing, sync, or extensive device testing are clearly marked as follow-up work.

## GSI edition

The repository now includes a **LineageOS-derived GSI customization-layer plan** under [`gsi/`](gsi/README.md), including a target manifest and a GitHub Actions metadata validation workflow.

- Huawei Y9 2019 (JKM-LX2): research-only; bootloader access and GSI compatibility are unconfirmed.
- Samsung Galaxy Tab S 10.5 (SM-T805 / chagalllte): research-only; kernel/vendor compatibility must be established.
- No flashable GSI is produced yet. A GSI is a generic system image, not a complete device-specific ROM or a substitute for a compatible kernel/vendor stack.
- The CI workflow validates metadata only; it does not build Android or certify device bootability.

See [GSI build status](gsi/BUILD_STATUS.md) before attempting any device modification.

## Device targets

- **Huawei Y9 2019 — JKM-LX2:** Android 9 / EMUI 9.1 (API 28) is the app compatibility baseline.
- **Samsung Galaxy Tab S 10.5 — SM-T805 / chagalllte:** Android 10 is the initial ROM research target.

An app that installs on a device does not imply that a ROM can be installed on it. ROM work must match the exact device variant, bootloader, recovery, kernel, vendor files and partition layout.

## Download built APKs

1. Open [GitHub Actions](https://github.com/tahmidgaming700/Experience-UI/actions).
2. Open the latest successful **Experience UI App Suite** run.
3. Download the artifact named `experience-ui-app-suite-and-boot-animation`.
4. The archive contains separate APKs in `apks/` and `bootanimation.zip` plus a GIF preview and the generator in `bootanimation/`.

These are debug-signed development APKs. Android may require you to allow installation from your chosen file manager. Do not install them over unrelated apps with the same package name/signing key.

## Build locally

Requirements: JDK 17, Android SDK Platform 35, Build Tools 35.0.0 and Gradle 8.9.

```sh
gradle assembleDebug
```

To generate the boot animation locally:

```sh
python3 -m pip install Pillow
python3 bootanimation/generate_bootanimation.py
```

The generator creates `bootanimation/bootanimation.zip` and `bootanimation/experience-boot-preview.gif`. The animation is designed for a 2560×1600 target such as the Tab S 10.5; review it on-device before integrating into a ROM.

## Integrating apps into LineageOS

For a maintained ROM build, the recommended approach is to add each app to the LineageOS source tree and build it with the ROM, rather than manually repacking a signed OTA ZIP. An ordinary APK cannot silently replace system apps or write protected partitions. A boot animation generally belongs at `/system/media/bootanimation.zip` or the appropriate product/system partition path for that ROM; the exact destination depends on the device and ROM build.

Before changing a ROM ZIP, inspect its structure, verify the checksum and confirm the package matches `chagalllte`. A repacked OTA may fail signature verification, and an incorrect image can make the tablet unbootable. Keep an untouched copy of the original package and a known-good recovery route.

## Safety and scope

- The current app builds are not privileged system apps.
- The apps do not unlock bootloaders, bypass screen locks, or flash ROMs.
- The dialler and SMS screens are not yet complete replacements for the system dialler/SMS handler.
- Do not mix firmware or ROM packages for the Huawei JKM-LX2 and Samsung chagalllte.
- Verify ROM signatures/checksums and back up personal data before any ROM experiment.

## Pico AI

Pico AI integration should be wired only after its actual installed package name and supported launch interface are confirmed. Do not embed account credentials in the app or repository.
