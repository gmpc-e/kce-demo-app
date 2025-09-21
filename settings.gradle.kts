// settings.gradle.kts

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    mavenLocal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenLocal()   // consume your :core-engine / :profiles SNAPSHOTs
    google()
    mavenCentral() // resolve transitive deps (if any) from Central
  }
}

rootProject.name = "kce-demo-app"
include(":app")

// (Optional) Composite build instead of mavenLocal()
// includeBuild("/Users/elkes/AndroidStudioProjects/kosherjava-compute-engine")