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

include(":core:resources")
include(":core:utils")

include(":data:dto")
include(":data:settings:api")
include(":data:settings:impl")
include(":data:settings:keys")
include(":data:repository:api")
include(":data:repository:room")

include(":feature:notification:api")
include(":feature:notification:impl")
include(":feature:tasks")
include(":feature:settings")
