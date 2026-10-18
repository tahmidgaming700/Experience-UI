# Experience UI prebuilt APKs + boot animation for LineageOS source builds.
# Copy the app suite into vendor/experience/prebuilt/ and bootanimation.zip into
# vendor/experience/media/ before inheriting this file from the device product makefile.
#
# Keep the ROM's stock apps until each replacement has been tested on-device.
# These APKs are normal system apps, NOT privileged apps.

PRODUCT_COPY_FILES += \
    vendor/experience/prebuilt/experience-launcher-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceLauncher/ExperienceLauncher.apk \
    vendor/experience/prebuilt/experience-setupwizard-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceSetupWizard/ExperienceSetupWizard.apk \
    vendor/experience/prebuilt/experience-dialer-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceDialer/ExperienceDialer.apk \
    vendor/experience/prebuilt/experience-messages-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceMessages/ExperienceMessages.apk \
    vendor/experience/prebuilt/experience-contacts-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceContacts/ExperienceContacts.apk \
    vendor/experience/prebuilt/experience-notes-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceNotes/ExperienceNotes.apk \
    vendor/experience/prebuilt/experience-clock-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceClock/ExperienceClock.apk \
    vendor/experience/prebuilt/experience-calendar-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceCalendar/ExperienceCalendar.apk \
    vendor/experience/prebuilt/experience-calculator-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceCalculator/ExperienceCalculator.apk \
    vendor/experience/prebuilt/experience-settings-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceSettings/ExperienceSettings.apk \
    vendor/experience/prebuilt/experience-files-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceFiles/ExperienceFiles.apk \
    vendor/experience/prebuilt/experience-gallery-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceGallery/ExperienceGallery.apk \
    vendor/experience/prebuilt/experience-camera-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceCamera/ExperienceCamera.apk \
    vendor/experience/prebuilt/experience-converter-debug.apk:$(TARGET_COPY_OUT_SYSTEM)/app/ExperienceConverter/ExperienceConverter.apk \
    vendor/experience/media/bootanimation.zip:$(TARGET_COPY_OUT_SYSTEM)/media/bootanimation.zip
