# OneHub Backend

NestJS + Prisma + PostgreSQL API for the OneHub control plane.

## Local development

```bash
# from repo root
docker compose up -d          # Postgres only
cd backend
cp .env.example .env          # set JWT secrets (≥32 chars)
npm ci
npx prisma generate
npx prisma migrate deploy
npm run prisma:seed
npm run start:dev             # http://localhost:3000
```

### Endpoints (Foundation)

| Method | Path | Auth |
|--------|------|------|
| GET | `/health` | public |
| GET | `/health/ready` | public (requires DB) |
| POST | `/auth/register` | public, rate-limited |
| POST | `/auth/login` | public, rate-limited |
| POST | `/auth/refresh` | public |
| POST | `/auth/logout` | public (revokes refresh) |
| GET | `/auth/me` | Bearer access token |
| GET | `/apps` | public |
| GET | `/apps/:appId` | public |
| POST/PATCH/DELETE | `/apps` | ADMIN / DEVELOPER |

## Production image (not published by default)

```bash
cd backend
docker build -t onehub-api .
# Provide secrets via env or orchestrator — never bake into the image
docker run --rm -p 3000:3000 \
  -e NODE_ENV=production \
  -e DATABASE_URL=... \
  -e JWT_ACCESS_SECRET=... \
  -e JWT_REFRESH_SECRET=... \
  -e CORS_ORIGIN=https://your-web.example \
  onehub-api
```

Optional compose profile (still local only):

```bash
docker compose --profile api up -d --build
```

## Migrations on deploy

```bash
npx prisma migrate deploy
```

Seed is for **dev/demo** only; do not auto-seed production without review.

## Security notes

- No JWT secrets in the repository
- Production refuses to start with short/missing JWT secrets
- Passwords hashed with Argon2; refresh tokens stored as SHA-256 hashes and rotated
- Platform accounts are **not** merged with per-app accounts
