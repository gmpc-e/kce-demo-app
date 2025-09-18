#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"/..
ENGINE="../kosherjava-compute-engine"
if [[ ! -x "${ENGINE}/gradlew" ]]; then
  echo "Engine gradlew not found at ${ENGINE}/gradlew"
  echo "Please adjust path or ensure the engine repo is a sibling directory."
  exit 1
fi
# Generate wrapper here using the engine's gradlew
"${ENGINE}/gradlew" -p . wrapper --gradle-version 8.9
chmod +x gradlew
echo "Gradle wrapper created. You can now run ./gradlew tasks"
