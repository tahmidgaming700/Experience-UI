# Termux: download and inspect a ROM safely

These commands download files and inspect the ROM ZIP; they do **not** flash or rewrite partitions. Full LineageOS builds are not practical on most Android tablets/phones because of storage, RAM, build-tool and kernel requirements. Use a supported Linux build machine for the ROM build itself.

## 1. Install Termux tools

Use Termux from its official source, then run:

```sh
pkg update && pkg upgrade
pkg install git gh curl wget unzip zip coreutils file
termux-setup-storage
mkdir -p "$HOME/ExperienceUI/downloads"
cd "$HOME/ExperienceUI/downloads"
```

## 2. Download the Experience UI APK and animation artifact

Sign in to GitHub CLI if needed:

```sh
gh auth login
gh run list --repo tahmidgaming700/Experience-UI --workflow "Experience UI App Suite" --limit 10
```

Pick a run whose conclusion is `success`, then substitute its numeric run ID:

```sh
RUN_ID=PUT_SUCCESSFUL_RUN_ID_HERE
gh run download "$RUN_ID" --repo tahmidgaming700/Experience-UI \
  --name experience-ui-app-suite-and-boot-animation \
  --dir "$HOME/ExperienceUI/artifact"
find "$HOME/ExperienceUI/artifact" -type f -maxdepth 4
```

If no successful run/artifact exists yet, wait for GitHub Actions to finish or inspect its build log first.

## 3. Download your exact ROM file

Copy the official/trusted direct download URL from the ROM maintainer's page. Do not use a URL for the Wi-Fi SM-T800 image on the LTE SM-T805.

```sh
ROM_URL='PASTE_THE_EXACT_SM-T805_CHAGALLLTE_ROM_URL_HERE'
curl -fL --retry 3 --retry-delay 2 "$ROM_URL" -o lineage-chagalllte.zip
sha256sum lineage-chagalllte.zip
unzip -t lineage-chagalllte.zip
unzip -l lineage-chagalllte.zip | head -80
```

Compare the SHA-256 output with the maintainer's published checksum. A successful `unzip -t` only confirms archive integrity, not that the ROM is authentic or compatible.

## 4. Stage the APKs and animation for a source build

Extract the downloaded GitHub artifact first. The artifact contains `apks/` and `bootanimation/`. Copy those files into a matching LineageOS source checkout on a supported Linux build machine, then run the repository's `rom-integration/prepare-prebuilts.sh` from the source tree:

```sh
bash /path/to/Experience-UI/rom-integration/prepare-prebuilts.sh \
  /path/to/extracted/experience-ui-app-suite-and-boot-animation
```

Then add this line to the product makefile for the **exact** device and ROM branch:

```make
$(call inherit-product, vendor/experience/experience_apps.mk)
```

Follow that branch's official build instructions. This installs the APKs as ordinary system apps and copies the boot animation into the build. It does not remove the stock apps or flash the tablet.

## Do not repack-and-flash the downloaded OTA ZIP

Manually adding APKs to the outside of an OTA ZIP does not integrate them into system images; directly replacing files inside a signed install ZIP may break its signature and updater-script assumptions. Use the ROM source build, or a verified device-specific image modification workflow with a recovery route and a full backup. Do not run random root scripts or write block devices by guessing partition names.
