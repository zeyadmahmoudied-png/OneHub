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
| Android Compose screens + Retrofit | Implemented |
| SDK package (`@onehub/sdk`) | Implemented |
| Gradle Wrapper 8.11.1 in Git | Implemented |
| package-lock.json (backend, web, sdk) | Implemented |
| CI: `npm ci` only (no install fallback) | Implemented |
| CI: Android assembleDebug + APK artifact | Implemented |
| PostgreSQL integration in CI | Implemented |

## How to run (local)

### Database
```bash
docker compose up -d
cd backend
cp .env.example .env
npm ci
npx prisma generate
npx prisma migrate deploy
npm run prisma:seed
npm run start:dev
```

### Web
```bash
cd apps/web
npm ci
npm run dev
```

### Android
```bash
cd apps/android
chmod +x gradlew
./gradlew :app:assembleDebug
```

### SDK
```bash
cd packages/sdk
npm ci && npm run build && npm test
```

## CI

Workflow: `.github/workflows/ci.yml`  
Actions: https://github.com/zeyadmahmoudied-png/OneHub/actions

Jobs: `backend` (Postgres), `web`, `sdk`, `android` (APK artifact `onehub-debug-apk`).

## Roadmap preserved

All 23 advanced features remain on the roadmap for Phases 2–7. Phase 1 does not remove any of them. Phase 2 has **not** started.
