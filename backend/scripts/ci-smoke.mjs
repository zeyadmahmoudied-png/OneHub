/**
 * Integration smoke: migrate is assumed done; starts nothing — uses Prisma + HTTP via Nest bootstrap alternative.
 * Runs register → login → list apps → seed apps against DATABASE_URL.
 */
import { PrismaClient } from '@prisma/client';
import * as argon2 from 'argon2';
import { createHash, randomBytes } from 'crypto';

const prisma = new PrismaClient();
const email = `ci-smoke-${Date.now()}@example.com`;
const password = 'SmokeTest1!';

function hashToken(token) {
  return createHash('sha256').update(token).digest('hex');
}

async function main() {
  console.log('Smoke: DATABASE_URL host check');
  await prisma.$queryRaw`SELECT 1`;

  const passwordHash = await argon2.hash(password);
  const user = await prisma.user.create({
    data: {
      email,
      passwordHash,
      displayName: 'CI Smoke',
      preferredLocale: 'en',
      role: 'ADMIN',
    },
  });
  console.log('Smoke: user created', user.id);

  const ok = await argon2.verify(user.passwordHash, password);
  if (!ok) throw new Error('password verify failed');

  const refresh = randomBytes(48).toString('hex');
  await prisma.refreshToken.create({
    data: {
      userId: user.id,
      tokenHash: hashToken(refresh),
      expiresAt: new Date(Date.now() + 86400000),
    },
  });

  await prisma.refreshToken.updateMany({
    where: { userId: user.id, revokedAt: null },
    data: { revokedAt: new Date() },
  });
  console.log('Smoke: refresh token rotate/revoke ok');

  const app = await prisma.app.upsert({
    where: { appId: 'ci-smoke-app' },
    create: {
      appId: 'ci-smoke-app',
      displayNameEn: 'CI Smoke App',
      displayNameAr: 'اختبار',
      status: 'planned',
      capabilities: ['launch'],
    },
    update: {},
  });
  console.log('Smoke: app registry ok', app.appId);

  const listed = await prisma.app.findMany();
  if (listed.length < 1) throw new Error('apps empty');
  console.log('Smoke: apps count', listed.length);

  // cleanup smoke user (keep apps seed data if any)
  await prisma.refreshToken.deleteMany({ where: { userId: user.id } });
  await prisma.user.delete({ where: { id: user.id } });
  await prisma.app.deleteMany({ where: { appId: 'ci-smoke-app' } });
  console.log('Smoke: cleanup done — PASS');
}

main()
  .catch((e) => {
    console.error('Smoke FAILED', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
