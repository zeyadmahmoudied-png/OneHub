# OneHub

**Unified platform for managing, operating, and integrating independent applications.**

OneHub is a multi-app control plane that keeps every application fully independent while providing a single place to discover, launch, monitor, integrate, and administer them.

> Designed from day one so a future **Control Center** (or any other independent app) can be registered and integrated without rewriting existing code.

## Core Principles

1. **App Independence** — Each app keeps its own data, users, roles, settings, premium system, backups, and release cycle.
2. **No APK-in-APK** — Integration uses Android Intents, App Links, deep links, APIs, SDK, and embedded WebViews where appropriate.
3. **Secure by default** — No secrets in client code. Strict RBAC, data isolation, audit logs.
4. **Extensible** — New apps are added via App Registry + Adapter/SDK without rebuilding the platform.
5. **Real functionality only** — No fake data, no non-working buttons.

## Managed Applications (Registry)

| App ID              | Display Name          | Status     |
|---------------------|-----------------------|------------|
| `my-plan`           | My Plan               | Planned    |
| `photo-cleaner`     | PhotoCleaner          | Planned    |
| `zoz-creator`       | ZOZ Creator           | Planned    |
| `dar-al-bayan`      | Dar Al Bayan          | Planned    |
| `youtube-creator`   | YouTube Creator       | Planned    |
| `control-center`    | Control Center        | Future     |

## Quick start (local)

### 1. PostgreSQL

```bash
docker compose up -d
# user/password/db from docker-compose.yml → onehub / onehub_dev_password / onehub
```

### 2. Backend

```bash
cd backend
cp .env.example .env   # set JWT secrets (≥32 chars)
npm ci
npx prisma generate
npx prisma migrate deploy
npm run prisma:seed
npm run start:dev      # http://localhost:3000
```

Health: `GET /health`  
Auth: `POST /auth/register`, `POST /auth/login`  
Apps: `GET /apps`

### 3. Web dashboard

```bash
cd apps/web
npm ci
# optional: NEXT_PUBLIC_API_URL=http://localhost:3000
npm run dev            # http://localhost:3001
```

### 4. Android

```bash
cd apps/android
chmod +x gradlew
./gradlew :app:assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Gradle Wrapper **8.11.1** is committed under `apps/android/`. Emulator reaches host API at `10.0.2.2:3000`.

### 5. SDK

```bash
cd packages/sdk
npm ci
npm run build
npm test
```

### 6. Tests

```bash
cd backend && npm test
cd packages/sdk && npm test
```

CI: `.github/workflows/ci.yml` — Backend + Postgres, Web, SDK, Android Debug APK (`onehub-debug-apk`).

## Repository structure

```
OneHub/
├── apps/android/     # Kotlin + Compose (Gradle Wrapper 8.11.1)
├── apps/web/         # Next.js 15
├── backend/          # NestJS + Prisma
├── packages/sdk/     # Integration SDK
├── docs/             # Architecture, roadmap, security, Phase 1 closeout
├── docker-compose.yml
└── .github/workflows/ci.yml
```

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [Roadmap](docs/ROADMAP.md) — includes the 23 advanced features for later phases
- [Database schema](docs/DATABASE_SCHEMA.md)
- [Security](docs/SECURITY.md)
- [Phase 1 status](docs/PHASE_1_STATUS.md)
- [Phase 1 closeout](docs/PHASE_1_CLOSEOUT.md)

## License

Private / TBD by owner.
