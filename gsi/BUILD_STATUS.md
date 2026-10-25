# GSI milestone status

## Milestone 0 — repository groundwork

- [x] Define GSI customization-layer scope.
- [x] Add target compatibility manifest for JKM-LX2 and chagalllte.
- [x] Add safe CI validation for manifest metadata.
- [x] Document a LineageOS/Treble-Droid source-build path and explain ARM64, A/B, vanilla and Google Apps variant names.
- [x] Replace the placeholder boot-animation design with an eight-stage storyboard matching the supplied sketch.
- [x] Expand Setup Wizard into a nine-step first-run flow.
- [ ] Confirm the exact upstream GSI source/branch and generated build target before a full sync/build.
- [ ] Record verified base image URL and SHA-256 if using a prebuilt base.
- [ ] Collect and review read-only device properties for each target.
- [ ] Confirm legitimate bootloader access for the exact model.
- [ ] Integrate the app suite and boot animation into a source-based product build.
- [ ] Build and boot-test a complete image for one confirmed target.

## Release gate

No full GSI has been built or boot-tested by this repository yet. The current commits improve app source, setup flow, boot-animation generation and build documentation; they do not constitute a bootable ROM. A target stays `research-only` until bootloader access, Treble state, ABI, partition layout, kernel/vendor compatibility and a recovery route are established. CI validates metadata and APKs only; it does not certify GSI bootability.
