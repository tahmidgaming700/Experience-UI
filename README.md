# Experience OS Converter

**One Android APK. One unified interface.** Experience OS Converter consolidates the planned Experience UI tools into one native Kotlin + Jetpack Compose application.

## App sections
- **Home:** device overview and shortcuts
- **Tools:** dialler, SMS composer, Android settings and Pico AI integration guidance
- **OS Converter:** device-specific compatibility and conversion planning for Huawei Y9 2019 (JKM-LX2) and Samsung Galaxy Tab S (SM-T805 / chagalllte)
- **Setup:** language and appearance preferences, Wi-Fi and security settings hand-offs
- **System:** recovery/ROM guidance and copyable diagnostics

## Device goals
- **Huawei Y9 2019 JKM-LX2:** app compatibility from Android 9 / EMUI 9.1 (API 28).
- **Samsung Galaxy Tab S SM-T805 (chagalllte):** initial custom ROM planning target Android 10.

The application itself uses `minSdk 28` and `targetSdk 35`. The app's Android API compatibility does not imply that a ROM can be installed on a device.

## Build
GitHub Actions builds **one APK** from the `:app` module and uploads it as the `experience-os-converter-debug-apk` artifact. Open Actions, choose the latest successful run, then download the artifact. Build locally with JDK 17, Android SDK Platform 35 and Gradle 8.9 using `gradle :app:assembleDebug`.

## Important platform limits
A normal APK cannot replace the OS, write system partitions, unlock a bootloader, silently flash recovery, install privileged RRO overlays, replace boot animations/system sounds, or bypass PIN/biometric protections. Those actions require a supported device-specific bootloader/recovery/ROM workflow and explicit user decisions. The app provides compatibility checks, planning, safe Android intents and diagnostics; it does not claim to perform privileged flashing.

## Pico AI
The app provides an integration entry point and guidance for connecting an already-installed Pico AI app. Its package/action must be confirmed before a direct launch integration can be safely hard-coded. The app never asks for AI account passwords.

## Security
Only use firmware/ROM packages that match the exact device variant and region, with verified checksums and trusted signing. Back up important data first. Do not flash a JKM-LX2 package onto chagalllte or vice versa.
