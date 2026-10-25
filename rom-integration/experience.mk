# Experience UI system integration for the LineageOS-derived A/B GSI.
PRODUCT_PACKAGES += \
    ExperienceLauncher \
    ExperienceSettings \
    ExperienceSetupWizard \
    ExperienceDialer \
    ExperienceMessages \
    ExperienceContacts \
    ExperienceNotes \
    ExperienceClock \
    ExperienceCalendar \
    ExperienceCalculator \
    ExperienceFiles \
    ExperienceGallery \
    ExperienceCamera \
    ExperienceConverter \
    ExperienceOSConverter

PRODUCT_COPY_FILES += \
    vendor/experience/media/bootanimation.zip:$(TARGET_COPY_OUT_SYSTEM)/media/bootanimation.zip

# Honest Experience UI identity properties; keep upstream LineageOS fingerprints intact.
PRODUCT_SYSTEM_PROPERTIES += \
    ro.experience.ui.name=ExperienceUI \
    ro.experience.ui.version=1.0.0 \
    ro.experience.ui.build.number=EXUI-1.0.0-$(shell date -u +%Y%m%d) \
    ro.experience.ui.base=LineageOS-GSI \
    ro.experience.ui.variant=$(EXPERIENCE_VARIANT)
