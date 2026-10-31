import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

const KNOWN_APPS = [
  {
    appId: 'my-plan',
    displayNameEn: 'My Plan',
    displayNameAr: 'خطتي',
    descriptionEn: 'Personal planning application',
    descriptionAr: 'تطبيق التخطيط الشخصي',
    status: 'planned',
    capabilities: ['launch'],
    adapterType: 'intent',
  },
  {
    appId: 'photo-cleaner',
    displayNameEn: 'PhotoCleaner',
    displayNameAr: 'منظف الصور',
    descriptionEn: 'Photo cleanup and organization',
    descriptionAr: 'تنظيف وتنظيم الصور',
    status: 'planned',
    capabilities: ['launch'],
    adapterType: 'intent',
  },
  {
    appId: 'zoz-creator',
    displayNameEn: 'ZOZ Creator',
    displayNameAr: 'صانع زوز',
    descriptionEn: 'Content creation tools',
    descriptionAr: 'أدوات إنشاء المحتوى',
    status: 'planned',
    capabilities: ['launch'],
    adapterType: 'intent',
  },
  {
    appId: 'dar-al-bayan',
    displayNameEn: 'Dar Al Bayan',
    displayNameAr: 'دار البيان',
    descriptionEn: 'Publishing and reading platform',
    descriptionAr: 'منصة النشر والقراءة',
    status: 'planned',
    capabilities: ['launch'],
    adapterType: 'intent',
  },
  {
    appId: 'youtube-creator',
    displayNameEn: 'YouTube Creator',
    displayNameAr: 'صانع يوتيوب',
    descriptionEn: 'YouTube content management',
    descriptionAr: 'إدارة محتوى يوتيوب',
    status: 'planned',
    capabilities: ['launch'],
    adapterType: 'intent',
  },
];

async function main() {
  for (const app of KNOWN_APPS) {
    await prisma.app.upsert({
      where: { appId: app.appId },
      create: app as any,
      update: {
        displayNameEn: app.displayNameEn,
        displayNameAr: app.displayNameAr,
        descriptionEn: app.descriptionEn,
        descriptionAr: app.descriptionAr,
      },
    });
  }
  console.log(`Seeded ${KNOWN_APPS.length} apps (upsert).`);
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
