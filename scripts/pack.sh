#!/usr/bin/env bash
set -euo pipefail

# ------------------------------------------------------------------------------
# Pack Android project source snapshot into a zip
# Includes: .kt files, Gradle build files, settings, properties, and README
# Excludes: build outputs, .git, caches, etc.
#
# Usage:
#   ./pack_demo_app_src.sh
# ------------------------------------------------------------------------------

PROJECT_DIR="/Users/elkes/AndroidStudioProjects/kce-demo-app"
OUT_NAME="kce-demo-app_sources_$(date +%Y%m%d_%H%M).zip"
OUT_PATH="${PROJECT_DIR}/scripts/${OUT_NAME}"

# Ensure output directory exists
mkdir -p "${PROJECT_DIR}/scripts"

# Go into project root
cd "$PROJECT_DIR"

# Create the zip
zip -r "$OUT_PATH" \
  $(find . -type f \( \
      -name "*.kt" -o \
      -name "*.gradle" -o \
      -name "*.gradle.kts" -o \
      -name "gradle.properties" -o \
      -name "settings.gradle" -o \
      -name "settings.gradle.kts" -o \
      -name "README*" \
    \)) \
  -x "*/build/*" "*/.gradle/*" "*/.idea/*" "*.iml" "*.zip"

echo "✅ Source snapshot created at: $OUT_PATH"
