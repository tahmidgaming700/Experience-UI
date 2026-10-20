# GSI milestone status

## Milestone 0 — repository groundwork

- [x] Define GSI customization-layer scope.
- [x] Add target compatibility manifest for JKM-LX2 and chagalllte.
- [x] Add safe CI validation for manifest metadata.
- [ ] Record verified LineageOS-derived base image URL and SHA-256.
- [ ] Collect and review read-only device properties for each target.
- [ ] Confirm bootloader-unlock path for the exact model.
- [ ] Select compatible overlay integration method.
- [ ] Build and test a complete image for one confirmed target.

## Release gate

No flashable image is produced by this milestone. A target stays `research-only` until bootloader access, Treble state, ABI, partition layout, kernel/vendor compatibility and a recovery route are established. CI validates metadata only; it is not an Android ROM build and does not certify bootability.
