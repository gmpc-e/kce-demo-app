plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  namespace = "com.elad.kce.demo"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.elad.kce.demo"
    minSdk = 28
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
  }

  // === JVM 17 everywhere ===
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs += listOf(
      "-Xjvm-default=all",
      "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
    )
  }
  kotlin {
    jvmToolchain(17)
  }

  buildFeatures { compose = true }

  // Compose Compiler 1.5.14 ↔ Kotlin 1.9.24 (compatible pair)
  composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

  packaging {
    resources {
      excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
  }
}

dependencies {
  // Engine (published to mavenLocal)
  implementation("com.elad.halacha:core-engine:0.1.2-SNAPSHOT")
  implementation("com.elad.halacha:profiles:0.1.2-SNAPSHOT")
  implementation("com.kosherjava:zmanim:2.5.0")

  // Compose
  implementation(platform("androidx.compose:compose-bom:2024.09.01"))
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")

  // Activity + Lifecycle
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
}

// print resolved engine coords on every build
tasks.register("printEngineVersions") {
  doLast {
    val confs = listOf(
      "debugRuntimeClasspath",
      "releaseRuntimeClasspath",
      "debugCompileClasspath",
      "releaseCompileClasspath",
    )
    logger.lifecycle("── Resolved Engine Artifacts ──")
    confs.forEach { c ->
      configurations.findByName(c)
        ?.resolvedConfiguration
        ?.firstLevelModuleDependencies
        ?.filter { it.moduleGroup == "com.elad.halacha" }
        ?.forEach { dep ->
          logger.lifecycle("$c: ${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}")
        }
    }
    logger.lifecycle("──────────────────────────────")
  }
}
tasks.named("preBuild").configure { dependsOn("printEngineVersions") }