# OneHub Backend

NestJS + Prisma API.

## Requirements

- Node.js 22+
- PostgreSQL (local or remote)

## Setup

```bash
cd backend
cp .env.example .env
# edit DATABASE_URL and JWT secrets
npm install
npx prisma generate
npx prisma migrate dev --name init
npm run start:dev
```

Health: `GET http://localhost:3000/health`  
Apps: `GET http://localhost:3000/apps`  
Seed known apps: `POST http://localhost:3000/apps/seed`

## Tests

```bash
npm test
```
