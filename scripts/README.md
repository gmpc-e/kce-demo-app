# KCE Demo App (Android, Jetpack Compose)

A lean demo that calls the KosherJava Compute Engine directly (no server):
- Fetch profiles from engine (`listProfiles()`).
- Compute by external name (`computeByExternalName(name, req)`).
- Show Hebrew labels with rounded `HH:mm` (seconds > 30 rounds up).

## Prereqs
- JDK 17 (set Android Studio -> Gradle JDK to 17)
- Engine repo exists at: `../kosherjava-compute-engine` (sibling path)

## First-time setup
```bash
# generate Gradle wrapper using the engine's wrapper (no Homebrew Gradle needed)
cd scripts
./bootstrap_wrapper.sh
```

Then import the project in Android Studio, or use:
```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

## Notes
- Adjust `includeBuild("../kosherjava-compute-engine")` in `settings.gradle.kts` if your engine lives elsewhere.
- If engine model names differ, tweak mapping in `EngineBridge.kt` only.
