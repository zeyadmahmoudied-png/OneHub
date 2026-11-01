# Foundation Activation Audit

**Repo:** https://github.com/zeyadmahmoudied-png/OneHub  
**Date:** 2026-11-01  
**Scope:** Activate and verify Phase 1 Foundation only. Phase 2 not started. Backend **not published**.

## Component matrix

| Component | Implemented | Works end-to-end | UI-only / incomplete | Needs hosting |
|-----------|-------------|------------------|----------------------|---------------|
| Backend Auth (register/login/refresh/logout/me) | Yes | Yes (local + CI smoke) | — | Yes for internet clients |
| App Registry CRUD + public list | Yes | Yes | Mutations need ADMIN/DEVELOPER | Yes |
| Roles + JWT + refresh rotation | Yes | Yes | — | Secrets on host |
| Audit logs on auth events | Yes | Yes | — | — |
| Prisma + migrations + seed | Yes | Yes in CI | — | Managed Postgres |
| Health `/health` + `/health/ready` | Yes | Ready checks DB | — | — |
| Dockerfile (prod image) | Yes | Buildable locally | **Not deployed** | — |
| Web Home / Apps / Detail | Yes | Yes against local API | Offline banner via errors | Points at `NEXT_PUBLIC_API_URL` |
| Web Login / Register / Logout | Yes | Yes with localStorage session | No HttpOnly cookies (Foundation) | — |
| Android screens + Retrofit | Yes | Emulator → `10.0.2.2:3000` | Physical device needs `-PAPI_BASE_URL` | Remote API URL |
| Android logout + refresh best-effort | Yes | Yes when API up | — | — |
| SDK contracts + client | Yes | Unit tests | Integration events stub only | — |

## What was activated in this pass

1. **Backend production readiness (not published)**  
   - `backend/Dockerfile`  
   - `GET /health/ready` (DB probe)  
   - Production bootstrap refuses weak JWT secrets  
   - Expanded `.env.example`  
   - `docker compose --profile api` optional local API container

2. **Android connectivity honesty**  
   - Debug default `http://10.0.2.2:3000` (emulator only)  
   - Override: `./gradlew assembleDebug -PAPI_BASE_URL=http://192.168.x.x:3000`  
   - Release requires explicit `API_BASE_URL`  
   - Friendly offline / refused-connection messages  
   - Logout + best-effort refresh on apps list  
   - Home shows live health probe result

3. **Web session**  
   - Shared `session.ts`  
   - Header shows user + logout (revokes refresh when possible)  
   - Clear offline error copy when Backend is down

## What still requires hosting (explicit)

| Need | Why |
|------|-----|
| Public Backend URL (HTTPS) | Mobile devices and remote browsers cannot reach `localhost` / `10.0.2.2` |
| Managed PostgreSQL | Production data durability |
| Strong JWT secrets | Required when `NODE_ENV=production` |
| CORS origin list | Restrict browser clients |

Until Backend is published, **online registration from a phone store APK will fail honestly** with a connection error — this is intentional.

## How to run Foundation locally (before publish)

```bash
# 1. DB
docker compose up -d

# 2. API
cd backend && cp .env.example .env && npm ci
npx prisma generate && npx prisma migrate deploy && npm run prisma:seed
npm run start:dev

# 3. Web
cd apps/web && npm ci && npm run dev

# 4. Android (emulator)
cd apps/android && ./gradlew :app:assembleDebug
# physical device:
# ./gradlew :app:assembleDebug -PAPI_BASE_URL=http://<LAN-IP>:3000
```

## Tests expected in CI

- Backend build + unit tests + Postgres migrate + `ci-smoke.mjs`
- Web typecheck + build
- SDK build + tests
- Android `assembleDebug` + `onehub-debug-apk` artifact

## Phase boundary

- **Phase 2 not started**
- **23 advanced features not implemented**
- **Backend not deployed to any paid host in this work**
