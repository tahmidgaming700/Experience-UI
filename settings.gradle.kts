pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ExperienceUI"
include(":ota-updater", ":core-ui", ":launcher", ":dialer", ":messages", ":settings", ":setupwizard", ":converter", ":contacts", ":notes", ":clock", ":calendar", ":calculator", ":files", ":gallery", ":camera")
