# Experience UI

Native Android application suite built with Kotlin, Jetpack Compose and Material 3.

## Modules
- `core-ui` — shared Material 3 theme and reusable components
- `launcher` — searchable app-grid concept
- `dialer` — dial pad handing calls to Android's system dialler
- `messages` — conversation-list UI foundation
- `settings` — dashboard linking to protected Android settings
- `setupwizard` — language, Wi-Fi, Pico AI, security and appearance setup flow
- `converter` — device compatibility information and a safe ROM checklist

## Device targets
- **Huawei Y9 2019 (JKM-LX2):** EMUI 9.1 / Android 9 application compatibility. This app baseline uses Android API 28.
- **Samsung Galaxy Tab S (SM-T805, chagalllte):** Android 10 is the initial custom-ROM target; the companion applications target Android 9+ where possible.

The app modules and a bootable ROM are separate deliverables. A bootable ROM requires device-specific source, a compatible kernel and vendor files, device configuration, recovery and hardware testing. An APK cannot convert a stock phone/tablet into a custom ROM by itself.

## Build
GitHub Actions builds debug APKs on pushes, pull requests and manual runs. Download the `experience-ui-debug-apks` artifact from a successful workflow run. For local builds, use JDK 17, Android SDK Platform 35 and Gradle 8.9, then run `gradle assembleDebug`.

## Safety and platform limits
Android may require explicit user confirmation or special roles for default dialer/SMS apps and protected settings. RRO overlays, system sounds, boot animations and system partitions require ROM/system integration or a device-specific privileged workflow. The converter is a checklist and compatibility helper; it does not flash devices.

## Pico AI
The setup flow includes a Pico AI onboarding step but does not collect passwords or falsely claim to install the separate Pico AI app. Integrate your existing Pico AI app through a verified app-to-app intent or documented interface when its package/action is confirmed.