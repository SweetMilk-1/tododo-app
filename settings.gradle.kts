pluginManagement {
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

rootProject.name = "ToDoDo"
include(":app")
include(":core:notifications:api")
include(":core:notifications:impl")
include(":core:resources")
include(":core:utils")

include(":data:dto")
include(":data:settings:api")
include(":data:settings:impl")
include(":data:repository:api")
include(":data:repository:room")

include(":feature:tasks")
include(":feature:settings")
include(":feature:notifications:api")
include(":feature:notifications:impl")
