rootProject.name = "cathopedia"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        // Android's system SQLite doesn't reliably ship FTS5 on every OS image
        // (confirmed missing on this project's own emulator). io.requery:sqlite-android
        // bundles a modern SQLite with FTS5 compiled in and is only published via JitPack.
        maven {
            url = uri("https://jitpack.io")
            content { includeGroup("com.github.requery") }
        }
    }
}

include(":androidApp")
include(":shared")
