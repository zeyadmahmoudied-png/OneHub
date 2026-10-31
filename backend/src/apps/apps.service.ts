import { Injectable, NotFoundException, ConflictException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateAppDto } from './dto/create-app.dto';
import { UpdateAppDto } from './dto/update-app.dto';

@Injectable()
export class AppsService {
  constructor(private readonly prisma: PrismaService) {}

  async findAll() {
    return this.prisma.app.findMany({
      orderBy: { displayNameEn: 'asc' },
    });
  }

  async findByAppId(appId: string) {
    const app = await this.prisma.app.findUnique({ where: { appId } });
    if (!app) {
      throw new NotFoundException(`App '${appId}' not found in registry`);
    }
    return app;
  }

  async create(dto: CreateAppDto) {
    const existing = await this.prisma.app.findUnique({ where: { appId: dto.appId } });
    if (existing) {
      throw new ConflictException(`App id '${dto.appId}' already registered`);
    }
    return this.prisma.app.create({
      data: {
        appId: dto.appId,
        displayNameEn: dto.displayNameEn,
        displayNameAr: dto.displayNameAr,
        descriptionEn: dto.descriptionEn,
        descriptionAr: dto.descriptionAr,
        packageName: dto.packageName,
        webUrl: dto.webUrl,
        iconUrl: dto.iconUrl,
        capabilities: dto.capabilities ?? [],
        adapterType: dto.adapterType ?? 'intent',
        status: dto.status ?? 'planned',
        currentVersion: dto.currentVersion,
      },
    });
  }

  async update(appId: string, dto: UpdateAppDto) {
    await this.findByAppId(appId);
    return this.prisma.app.update({
      where: { appId },
      data: {
        displayNameEn: dto.displayNameEn,
        displayNameAr: dto.displayNameAr,
        descriptionEn: dto.descriptionEn,
        descriptionAr: dto.descriptionAr,
        packageName: dto.packageName,
        webUrl: dto.webUrl,
        iconUrl: dto.iconUrl,
        capabilities: dto.capabilities,
        adapterType: dto.adapterType,
        status: dto.status,
        currentVersion: dto.currentVersion,
        healthScore: dto.healthScore,
      },
    });
  }

  async remove(appId: string) {
    await this.findByAppId(appId);
    return this.prisma.app.delete({ where: { appId } });
  }

  /** Seed the five known apps if registry is empty (idempotent). */
  async seedKnownApps() {
    const count = await this.prisma.app.count();
    if (count > 0) return { seeded: false, message: 'Registry already has apps' };

    const known = [
      {
        appId: 'my-plan',
        displayNameEn: 'My Plan',
        displayNameAr: 'خطتي',
        status: 'planned',
        capabilities: ['launch'],
      },
      {
        appId: 'photo-cleaner',
        displayNameEn: 'PhotoCleaner',
        displayNameAr: 'منظف الصور',
        status: 'planned',
        capabilities: ['launch'],
      },
      {
        appId: 'zoz-creator',
        displayNameEn: 'ZOZ Creator',
        displayNameAr: 'صانع زوز',
        status: 'planned',
        capabilities: ['launch'],
      },
      {
        appId: 'dar-al-bayan',
        displayNameEn: 'Dar Al Bayan',
        displayNameAr: 'دار البيان',
        status: 'planned',
        capabilities: ['launch'],
      },
      {
        appId: 'youtube-creator',
        displayNameEn: 'YouTube Creator',
        displayNameAr: 'صانع يوتيوب',
        status: 'planned',
        capabilities: ['launch'],
      },
    ];

    for (const app of known) {
      await this.prisma.app.create({ data: app as any });
    }
    return { seeded: true, count: known.length };
  }
}
