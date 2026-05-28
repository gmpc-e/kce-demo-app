pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "kce-demo-app"
include(":app")

// Composite build — picks up engine source directly, no publishToMavenLocal needed
includeBuild("../kosherjava-compute-engine")
