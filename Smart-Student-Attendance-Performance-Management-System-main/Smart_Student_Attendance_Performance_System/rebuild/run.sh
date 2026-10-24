#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mkdir -p out
if ! command -v javac >/dev/null 2>&1; then
  echo "JDK not found. Install JDK 17 or later and ensure javac is on PATH." >&2
  exit 1
fi
javac -encoding UTF-8 -d out src/main/java/com/smartcampus/Main.java
java -cp out com.smartcampus.Main
