pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NewsWise"

include(":app")

// Core Modules
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:domain")
include(":core:navigation")
include(":core:network")
include(":core:testing")
include(":core:ui")

// Feature Modules
include(":feature:bookmarks")
include(":feature:detail")
include(":feature:headlines")
include(":feature:search")