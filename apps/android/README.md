# OneHub Android

Kotlin + Jetpack Compose client for the OneHub control plane.

## API base URL

| Target | Default / how to set |
|--------|----------------------|
| **Emulator (debug)** | `http://10.0.2.2:3000` (host machine localhost) |
| **Physical device** | `./gradlew :app:assembleDebug -PAPI_BASE_URL=http://<PC-LAN-IP>:3000` |
| **Release** | Must pass `-PAPI_BASE_URL=https://your-api` — empty URL is refused at runtime |

Backend is **not published** by default. Without a reachable API, login/register show an honest connection error.

## Build

```bash
chmod +x gradlew
./gradlew :app:assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Features (Foundation)

- Home with live `/health` probe
- Register / Login / Logout (refresh revoked when possible)
- Apps list + detail from App Registry
- Session in DataStore; best-effort token refresh on apps load
