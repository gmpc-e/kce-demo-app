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

// Use local engine source (sibling repo):
// Adjust the path if your engine is elsewhere.
includeBuild("../kosherjava-compute-engine")
