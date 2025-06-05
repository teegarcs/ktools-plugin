pluginManagement {
    includeBuild("./build-logic")
    includeBuild("./gradle-plugin")
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
        maven {
            name = "LocalRoot"
            url = uri(rootProject.projectDir.path+"/libs")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "LocalRoot"
            url = uri(rootProject.projectDir.path+"/libs")
        }
    }
}

rootProject.name = "ktools"
include(":app")
include(":compiler-plugin")
include(":compiler-plugin:ktools-plugin")
include(":compiler-plugin:ktools-runtime")
