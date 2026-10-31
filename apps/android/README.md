# OneHub Android

Kotlin + Jetpack Compose + Material 3 client.

## Requirements

- Android Studio Ladybug+ or JDK 17 + Android SDK 35
- Backend running (emulator uses `10.0.2.2:3000`)

## Build Debug APK

```bash
cd apps/android
# Generate wrapper if missing (from Android Studio or):
# gradle wrapper --gradle-version 8.11.1
./gradlew :app:assembleDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Notes

- Cleartext HTTP enabled for local development only.
- Session tokens stored in DataStore Preferences.
- RTL is enabled via `supportsRtl`.
