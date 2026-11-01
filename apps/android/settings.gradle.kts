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

rootProject.name = "OneHub"

include(":app")
include(":core")
include(":navigation")
include(":accesscontrol")
// Feature slots prepared for Phase 3+ (no app source claimed complete here)
include(":features:slots")
