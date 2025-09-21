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
    google()
    mavenCentral()
    mavenLocal()
  }
}

rootProject.name = "kce-demo-app"
include(":app")

// Include the engine as a composite build and SUBSTITUTE module coords -> engine projects
includeBuild("../kosherjava-compute-engine") {
  dependencySubstitution {
    substitute(module("com.elad.halacha:core-engine"))
      .using(project(":core-engine"))
    substitute(module("com.elad.halacha:profiles"))
      .using(project(":profiles"))
  }
}
