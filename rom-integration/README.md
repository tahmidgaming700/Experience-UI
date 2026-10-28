# Experience UI source integration

This integration packages the Experience UI app suite into a LineageOS-derived Android source build. The app modules in `Android.mk` are marked as privileged APK modules (`LOCAL_PRIVILEGED_MODULE := true`), so Android's build places them under the privileged-app partition path when included in a product.

**Important:** placement in `priv-app` does not itself grant permissions, make an app the default handler, or confer platform-signature access. The current app manifests request few or no platform permissions, and the debug APKs produced by the current CI workflow are not signed with the ROM's private platform certificate. Therefore, this change is an integration step—not a claim that every app already has every privilege or is ready to replace Android's core apps.

## Source-tree integration

Build the app suite and boot animation using the Experience UI app-suite workflow, then extract its artifact. The artifact contains `apks/Experience-*.apk` and `bootanimation/bootanimation.zip`.

From the root of a matching LineageOS GSI source checkout:

```bash
mkdir -p vendor/experience/prebuilt vendor/experience/media
cp /path/to/artifact/apks/Experience-*.apk vendor/experience/prebuilt/
cp /path/to/artifact/bootanimation/bootanimation.zip vendor/experience/media/
cp /path/to/Experience-UI/rom-integration/Android.mk vendor/experience/Android.mk
cp /path/to/Experience-UI/rom-integration/experience.mk vendor/experience/experience.mk
```

Add this to the product makefile actually inherited by the chosen lunch target (verify its path in the checked-out source branch):

```make
include vendor/experience/experience.mk
```

## Permission policy and signing

Do not create a blanket allowlist for every privileged permission. Android's privileged-permission allowlist must contain only permissions that an app actually requests and that the ROM intentionally authorizes. Add the policy XML to the platform's product configuration (typically under `system/etc/permissions/privapp-permissions-experience.xml`, with the appropriate product copy rule) only after reviewing each app manifest and API usage. Keep the allowlist scoped to Experience package names and the minimum necessary permissions; a permission in XML does not replace a manifest declaration or runtime consent where required.

For a release build, sign apps that require platform-signature permissions with the platform certificate generated and controlled by the ROM build environment. Do not commit private signing keys to GitHub or place them in public CI artifacts. `PRESIGNED` in `Android.mk` preserves each APK's existing signature; it does not re-sign it with the platform key. Platform signing also does not bypass Android's permission model or SELinux.

## Default app roles and framework integration

Default HOME, dialer, SMS, contacts, and camera handling must be configured and verified using Android's role/intent resolution and the relevant framework contracts. Merely installing an APK does not assign the HOME, DIALER, or SMS role. The current Experience Dialer and Messages manifests are launchable apps, not complete replacements for Telecom/InCallService or the SMS role contract; do not set them as system defaults until those implementations pass role and integration tests. Keep upstream core apps available until replacements are proven.

A true system replacement also requires framework/resource overlays, permission declarations, services, intents, SELinux policy where applicable, and device/HAL compatibility. These files alone do not constitute a complete Android source build.

## GSI and device compatibility

A GSI is a generic system image, not a complete device ROM. The intended build family is ARM64 A/B/system-as-root, but lunch targets vary by LineageOS branch. Verify the exact target exists in the checked-out source tree. Do not flash until Treble/VNDK, ABI, partition layout, bootloader, vendor image and recovery compatibility have been independently verified.

## Identity and release safety

Only `ro.experience.ui.*` identity values should be changed. Preserve the upstream build fingerprint, build ID, security patch level and signing identity; do not represent a community build as an official upstream release. A successful file-injection check is not proof that the image boots or that any device is supported.
