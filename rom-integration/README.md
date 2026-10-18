# LineageOS integration notes

This is a source-build integration template, not a script that flashes or repacks a downloaded OTA ZIP.

## Important device check

The LineageOS device wiki currently lists **SM-T805 / chagalllte** with LineageOS 14.1 (Android 7.1) as its version, while newer Android versions may exist as third-party builds. Do not assume an arbitrary Android 10 ZIP is compatible. Confirm the source, exact model, required bootloader/recovery and the ROM's build instructions before using it. Official device reference: https://wiki.lineageos.org/devices/chagalllte/

## Prepare the source tree

From the root of a matching LineageOS source checkout, create:

- `vendor/experience/prebuilt/` — put the APKs from the GitHub Actions artifact here.
- `vendor/experience/media/bootanimation.zip` — put the generated animation here.
- Copy `experience_apps.mk` to `vendor/experience/experience_apps.mk`.

Then add this line to the **device product makefile for the exact target** (the file name differs by branch/device):

```make
$(call inherit-product, vendor/experience/experience_apps.mk)
```

Build the ROM using the matching LineageOS branch's documented build instructions. The makefile installs the Experience APKs as ordinary system apps and places the animation at `/system/media/bootanimation.zip`.

## Keep stock apps initially

Do not remove the ROM's existing launcher, dialler, SMS, contacts, calendar, camera, gallery or file manager yet. Several Experience apps are currently starter implementations that delegate to existing Android apps; the messages and dialler apps are not complete default-role replacements. Removing the original apps too early could leave the ROM without working core functions. Test boot, app launching, calls, SMS, contacts, camera, storage, alarms and recovery before considering removal.

These APKs are debug-signed and not privileged. Do not put them in `priv-app` or grant privileged permissions without implementing and reviewing the required permission allowlists and Android role contracts.

## Boot animation

The generated ZIP contains `desc.txt` and sequential PNG frames in `part0/`. It is stored without ZIP compression as expected by Android boot animation loading. Preview and test it before flashing; screen scaling and boot timing can vary across builds.
