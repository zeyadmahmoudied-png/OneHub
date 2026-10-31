# Phase 1 — Foundation Status

**Repo**: https://github.com/zeyadmahmoudied-png/OneHub  
**Updated**: 2026-10-31

## Summary table

| Element | Status |
|---------|--------|
| Backend Auth (register/login/refresh/logout/me) | Implemented |
| Argon2 password hashing | Implemented |
| JWT access + hashed refresh tokens + rotation | Implemented |
| Roles (USER/DEVELOPER/ADMIN) + RolesGuard | Implemented |
| Rate limiting (Throttler) on auth routes | Implemented |
| App Registry pagination/filter + role-protected mutations | Implemented |
| Prisma schema + SQL migration + seed | Implemented |
| Docker Compose PostgreSQL | Implemented |
| Auth + Apps unit tests | Implemented |
| Web (Next.js) Home/Apps/Detail/Login/Register | Implemented |
| Web uses live API (no fake data) | Implemented |
| Android Compose foundation + screens + Retrofit | Implemented |
| SDK package (@onehub/sdk) | Implemented |
| CI: backend build/test, web build, sdk build | Configured |
| Android CI / Debug APK produced in this environment | **Not run** (no Android SDK in agent environment) |
| End-to-end against live Postgres in this environment | **Depends on local Docker** |

## How to run (local)

### Database
```bash
docker compose up -d
cd backend
cp .env.example .env
npm install
npx prisma generate
npx prisma migrate deploy   # or migrate dev
npm run prisma:seed
npm run start:dev
```

### Web
```bash
cd apps/web
npm install
# optional: export NEXT_PUBLIC_API_URL=http://localhost:3000
npm run dev
```

### Android
Open `apps/android` in Android Studio and Run, or:
```bash
cd apps/android
./gradlew :app:assembleDebug   # after gradle wrapper is present
```

### SDK
```bash
cd packages/sdk
npm install && npm run build
```

## CI

Workflow: `.github/workflows/ci.yml`  
Actions URL: https://github.com/zeyadmahmoudied-png/OneHub/actions

Jobs: `backend`, `web`, `sdk`. Android intentionally omitted until Gradle wrapper + SDK image are added.

## Not done / deferred (honest)

1. **Live Postgres + migrate + API smoke** inside the agent sandbox (Docker not available / npm install slow).
2. **Debug APK artifact** — structure is buildable in Android Studio; agent has no Android SDK.
3. **Gradle Wrapper** binary not committed (generate via Android Studio or `gradle wrapper`).
4. **package-lock.json** files not committed yet (generated on first `npm install` in each package).
5. Full integration tests with Testcontainers — later phase.

## Roadmap preserved

All 23 advanced features remain on the roadmap for Phases 2–7. Phase 1 does not remove any of them.
