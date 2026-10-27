# Experience UI

**A modular Android app suite for the Experience UI ROM project.** Each core experience is a separate installable APK, sharing a Material 3 design system. GitHub Actions builds the app suite and generates the Experience UI boot animation.

## Apps in this milestone

| APK / module | Current scope |
|---|---|
| Launcher | Blue glass-panel home screen, real installed-app icons, app shortcuts, search/filter, date/time card and launchable app grid; live weather remains follow-up work |
| Setup Wizard | Nine-step first-run flow: introduction, language, region, Wi-Fi, data migration choice, Experience Account, Google services, appearance and finish; account/migration screens are honest hand-offs/placeholders until integrated into a ROM |
| Phone / Dialer | Number pad and safe hand-off to the system dialler |
| Messages | Phone-number/message composer that hands a draft to the installed SMS app; inbox/default-SMS integration remains future work |
| Contacts | Opens the device's contacts provider |
| Notes | Simple note saved locally on the device |
| Clock | Shows current time and opens system alarms/timer |
| Calendar | Opens calendar and hands off event creation to a calendar provider |
| Calculator | Local expression calculator with brackets, decimals, operator precedence, unary signs and percentages |
| Settings | Shortcuts to Android network, display, security, apps and device information settings |
| Files | Android document picker |
| Gallery | Android photo/video picker |
| Camera | Opens the installed camera app |
| OS Converter | Device-specific ROM planning checklist; it does not flash firmware |

Shared UI lives in `:core-ui`. These are **early app foundations**, not yet a complete replacement for every Google/Samsung system application. The app-overhaul branch is replacing decorative placeholders with working, clearly scoped flows; features requiring privileged Android roles remain explicitly labelled. Features requiring privileged system APIs, a complete dialler/SMS role implementation, camera hardware integration, calendar/contact databases, media indexing, sync, or extensive device testing are clearly marked as follow-up work.

## Experience Launcher preview

The launcher home screen now uses a deep-blue gradient wallpaper, translucent rounded app panels (Quick Apps, My Apps, Quick Tools), a searchable app area, and a date/time card. It discovers launchable apps installed on the device and launches them where Android permits. The weather area is an honest placeholder until a weather provider is integrated; it does not claim to show live conditions. This is an installable launcher app, not a privileged replacement for the entire Android system UI.

## Boot animation and setup flow\n\nThe boot animation generator now follows the eight-stage storyboard supplied for Experience UI: abstract mark stages, a Harmony wordmark stage, the Experience UI wordmark, and a final “powered by Android” frame. It creates a 1080×1920 animation plus a small GIF preview. Review the generated artifact before integrating it into a ROM.\n\nThe Setup Wizard now has a nine-step first-run flow and stores selected preferences locally. Wi-Fi and Google account steps hand off to Android settings. Data migration and Experience Account are not yet system-integrated services, and Google Apps are not installed by selecting the Google option.\n\n## Current app-overhaul work

- Calculator: redesigned keypad and expression display; local parser supports arithmetic precedence, parentheses, unary signs, decimal numbers and percentages. Input is parsed as arithmetic and never executed as code.
- Messages: real recipient/message draft screen that hands off to the installed SMS app. It does not pretend to have an inbox or send SMS silently.
- These changes are in `feature/experience-ui-app-overhaul` pending CI validation. Do not call the resulting APK suite a complete OS until launcher/system UI, privileged roles, device integration and boot validation are complete.

## GSI edition

The repository now includes a **LineageOS-derived GSI build plan** under [`gsi/`](gsi/README.md), including [`BUILD_GSI.md`](gsi/BUILD_GSI.md), a target manifest and a GitHub Actions metadata validation workflow.

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
