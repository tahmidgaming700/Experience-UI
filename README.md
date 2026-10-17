# Experience OS Converter

**One APK · one unified interface.** Experience UI's launcher prompt, device conversion planner, setup guidance, and everyday tool hand-offs are being consolidated into the single native Kotlin + Jetpack Compose app in `:app`.

## Included screens

- **Home:** Android/device overview and quick actions.
- **OS Converter:** target selection and device-specific ROM preparation checklist.
- **Default apps:** prompts to make Experience UI the Home app and opens Android's default-app settings.
- **Tools:** opens the system dialler, SMS composer, Android Settings, and this app's settings.
- **Setup:** guided steps and safe hand-offs to protected Android settings.

## Device targets

- **Huawei Y9 2019 — JKM-LX2:** Android 9 / EMUI 9.1 (API 28) is the app's minimum compatibility baseline.
- **Samsung Galaxy Tab S — SM-T805 / chagalllte:** Android 10 is the initial custom-ROM research target.

The app uses `minSdk 28`, `targetSdk 35`, JDK 17, Kotlin, and Jetpack Compose. Being installable on a device does **not** mean the app can convert that device's operating system.

## Default-app prompts and limits

The app registers as a Home/Launcher candidate and asks Android to show its official Home-role confirmation (Android 10+) or Home settings (Android 9). The user must approve the change.

Phone and SMS defaults are different roles. The current build hands off calls and texts to the installed system apps; it is **not yet a full default dialler or default SMS handler**, so Android may not offer it for those roles. That requires implementing and testing the complete Android dialler/SMS role contracts, not just showing a prompt. No default is changed silently.

## Build one APK

GitHub Actions builds `:app:assembleDebug` and uploads the single APK as `experience-os-converter-debug-apk`. Open the repository's **Actions** tab, choose a successful run, and download its artifact. For a local build, use JDK 17, Android SDK Platform 35, Build Tools 35.0.0 and Gradle 8.9:

```sh
gradle :app:assembleDebug
```

The legacy module directories may remain in the repository for reference, but `settings.gradle.kts` includes only `:app`; the workflow builds one APK.

## ROM conversion is not a one-tap operation

A normal APK cannot replace Android itself, write protected system partitions, unlock a bootloader, silently flash a recovery, install privileged runtime resource overlays, or replace system boot assets. Those require a supported device-specific ROM/recovery workflow and appropriate privileges. This app currently provides planning, system prompts and safe intents; it does **not** claim to flash or install a ROM.

For any ROM work, verify the exact model, regional variant, bootloader/recovery support, device tree, kernel and vendor files. Back up data and verify package checksums/signing. Never flash a JKM-LX2 package on `chagalllte`, or the reverse.

## Pico AI

Direct launch integration stays unconfigured until Pico AI's installed package name and supported launch action are confirmed. The app never asks for AI account passwords.
