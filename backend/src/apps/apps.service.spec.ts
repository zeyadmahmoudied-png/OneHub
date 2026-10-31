import { Test, TestingModule } from '@nestjs/testing';
import { AppsService } from './apps.service';
import { PrismaService } from '../prisma/prisma.service';
import { NotFoundException, ConflictException } from '@nestjs/common';

describe('AppsService', () => {
  let service: AppsService;
  let prisma: {
    app: {
      findMany: jest.Mock;
      findUnique: jest.Mock;
      create: jest.Mock;
      update: jest.Mock;
      delete: jest.Mock;
      count: jest.Mock;
    };
  };

  beforeEach(async () => {
    prisma = {
      app: {
        findMany: jest.fn(),
        findUnique: jest.fn(),
        create: jest.fn(),
        update: jest.fn(),
        delete: jest.fn(),
        count: jest.fn(),
      },
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AppsService,
        { provide: PrismaService, useValue: prisma },
      ],
    }).compile();

    service = module.get<AppsService>(AppsService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('findAll returns list from prisma', async () => {
    const apps = [{ appId: 'my-plan', displayNameEn: 'My Plan' }];
    prisma.app.findMany.mockResolvedValue(apps);
    const result = await service.findAll();
    expect(result).toEqual(apps);
    expect(prisma.app.findMany).toHaveBeenCalled();
  });

  it('findByAppId throws when missing', async () => {
    prisma.app.findUnique.mockResolvedValue(null);
    await expect(service.findByAppId('unknown')).rejects.toBeInstanceOf(NotFoundException);
  });

  it('create throws ConflictException on duplicate appId', async () => {
    prisma.app.findUnique.mockResolvedValue({ appId: 'my-plan' });
    await expect(
      service.create({
        appId: 'my-plan',
        displayNameEn: 'My Plan',
        displayNameAr: 'خطتي',
      }),
    ).rejects.toBeInstanceOf(ConflictException);
  });
});
