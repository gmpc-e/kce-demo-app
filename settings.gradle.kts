// settings.gradle.kts (root)

pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    mavenLocal() // allow AGP/Kotlin plugins from local if ever needed
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenLocal()   // <-- consume engine artifacts from here
    google()
    mavenCentral()
  }
}

rootProject.name = "kce-demo-app"

// Only the Android app lives in this build.
include(":app")

// (Optional alternative to mavenLocal: composite build of the engine repo)
// If you prefer composite (no publish step), uncomment:
// includeBuild("/Users/elkes/AndroidStudioProjects/kosherjava-compute-engine")