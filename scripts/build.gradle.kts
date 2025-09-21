// build.gradle.kts (root)

plugins {
  id("com.android.application") version "8.7.1" apply false
  id("org.jetbrains.kotlin.android") version "1.9.24" apply false

}

tasks.register("clean", Delete::class) {
  delete(layout.buildDirectory)
}