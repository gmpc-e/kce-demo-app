# kce-demo-app — Claude Working Reference

## Role
Android Jetpack Compose demo app (app name: **Zmanim** / **זמנים**) that consumes
`kosherjava-compute-engine` in-process (no HTTP). Part of the **Zmanim** project.

GitHub: `gmpc-e/kce-demo-app`

## Tech Stack
- Kotlin 1.9.24, JVM 17
- Android minSdk 28, compileSdk/targetSdk 35
- Jetpack Compose UI, Hebrew RTL layout
- applicationId: `com.elad.kce.demo`
- Engine consumed via Gradle composite build (no publishToMavenLocal needed)

## Engine Integration
`settings.gradle.kts` includes the engine as a composite build:
```kotlin
includeBuild("../kosherjava-compute-engine")
```
The engine is on the local filesystem at `../kosherjava-compute-engine`. Changes to the
engine are picked up automatically on next build — no publish step required.

## Module Layout
```
app/src/main/java/com/elad/kce/demo/
  EngineBridge.kt      — sole entry point to the engine (object singleton)
  MainViewModel.kt     — AndroidViewModel; holds UiState, drives all engine calls
  MainActivity.kt      — Compose host activity
  Models.kt            — City, UiProfile, ZmanItem data classes
  ui/components/       — Compose UI components
  ui/theme/            — Color, Theme
app/src/main/assets/
  cities.json          — city list (name, lat, lon, elev, tz, candleMinutes)
  profiles_index.json  — fallback profile list when engine returns empty
```

## Key Files
| File | Purpose |
|------|---------|
| `EngineBridge.kt` | Wraps `ProfilesServiceImpl`; exposes `listProfiles()` and `computeProfile()`. Falls back to `profiles_index.json` asset if engine fails. |
| `MainViewModel.kt` | All engine calls go via `viewModelScope + Dispatchers.Default`. Holds `UiState` as Compose `mutableStateOf`. |
| `Models.kt` | `City`, `UiProfile`, `ZmanItem` — pass these between ViewModel and UI |

## Engine Bridge Rules
- `EngineBridge` is the **only** place that imports engine classes. UI and ViewModel never import engine packages directly.
- Engine calls are always off the main thread: `withContext(Dispatchers.Default) { EngineBridge.* }`.
- `EngineBridge.computeProfile()` returns `Result<List<ZmanItem>>` — use `fold` in ViewModel.

## Active Profiles
- `or-hachaim-sefardi` — Or HaChaim Sefardi shita
- `chazon-shamaim` — Hazon Shamaim shita

Profile keys come from the engine at runtime; `assets/profiles_index.json` is a static fallback.

## Build Commands
```bash
./gradlew :app:assembleDebug          # build debug APK
./gradlew :app:installDebug           # build + install on connected device/emulator
./gradlew :app:test                   # unit tests
./gradlew :app:connectedAndroidTest   # instrumented tests
```

## Branching Strategy
- `main` — stable, releasable
- `feat/*` — feature branches; merge to main via PR
