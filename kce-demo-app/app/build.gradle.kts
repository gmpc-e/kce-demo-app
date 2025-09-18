plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("plugin.serialization")
}

android {
  namespace = "com.elad.kce.demo"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.elad.kce.demo"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "0.1.0"

    vectorDrawables { useSupportLibrary = true }
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.15"
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    isCoreLibraryDesugaringEnabled = true
  }

  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  // Compose BOM & libs
  val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
  implementation(composeBom)
  androidTestImplementation(composeBom)

  implementation("androidx.activity:activity-compose:1.9.2")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")

  // KotlinX Serialization
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

  // Date/Time desugaring
  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")

  // Depend on the engine modules via included build:
  implementation(project(":core-engine"))
  implementation(project(":profiles"))
}
