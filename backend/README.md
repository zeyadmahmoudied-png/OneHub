# OneHub Backend

NestJS + Prisma API.

## Requirements

- Node.js 22+
- PostgreSQL (use root `docker compose up -d`)

## Setup

```bash
# from repo root
docker compose up -d

cd backend
cp .env.example .env
npm install
npx prisma generate
npx prisma migrate deploy
npm run prisma:seed
npm run start:dev
```

## Endpoints

| Method | Path | Auth |
|--------|------|------|
| GET | `/health` | public |
| POST | `/auth/register` | public (rate limited) |
| POST | `/auth/login` | public (rate limited) |
| POST | `/auth/refresh` | public |
| POST | `/auth/logout` | public |
| GET | `/auth/me` | Bearer access token |
| GET | `/apps` | public (supports `?q=&status=&page=&limit=`) |
| GET | `/apps/:appId` | public |
| POST | `/apps` | ADMIN or DEVELOPER |
| PATCH | `/apps/:appId` | ADMIN or DEVELOPER |
| DELETE | `/apps/:appId` | ADMIN |
| POST | `/apps/seed` | ADMIN |

## Tests

```bash
npm test
```
