import { Test, TestingModule } from '@nestjs/testing';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { ConflictException, UnauthorizedException } from '@nestjs/common';
import { AuthService } from './auth.service';
import { PrismaService } from '../prisma/prisma.service';
import * as argon2 from 'argon2';

jest.mock('argon2', () => ({
  hash: jest.fn().mockResolvedValue('hashed'),
  verify: jest.fn(),
}));

describe('AuthService', () => {
  let service: AuthService;
  let prisma: any;
  let jwt: { signAsync: jest.Mock };

  beforeEach(async () => {
    prisma = {
      user: {
        findUnique: jest.fn(),
        create: jest.fn(),
      },
      refreshToken: {
        create: jest.fn().mockResolvedValue({}),
        findFirst: jest.fn(),
        update: jest.fn(),
      },
      auditLog: {
        create: jest.fn().mockResolvedValue({}),
      },
    };
    jwt = { signAsync: jest.fn().mockResolvedValue('access-token') };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        { provide: PrismaService, useValue: prisma },
        { provide: JwtService, useValue: jwt },
        {
          provide: ConfigService,
          useValue: {
            get: (key: string) => {
              if (key === 'JWT_ACCESS_SECRET') return 'test-secret-32-characters-long!!';
              if (key === 'JWT_REFRESH_DAYS') return '30';
              return undefined;
            },
          },
        },
      ],
    }).compile();

    service = module.get(AuthService);
  });

  it('register throws ConflictException when email exists', async () => {
    prisma.user.findUnique.mockResolvedValue({ id: '1', email: 'a@b.com' });
    await expect(
      service.register({
        email: 'a@b.com',
        password: 'password12',
        displayName: 'Test',
      }),
    ).rejects.toBeInstanceOf(ConflictException);
  });

  it('register creates user and returns tokens', async () => {
    prisma.user.findUnique.mockResolvedValue(null);
    prisma.user.create.mockResolvedValue({
      id: 'u1',
      email: 'new@example.com',
      displayName: 'New',
      role: 'USER',
      preferredLocale: 'ar',
    });

    const result = await service.register({
      email: 'new@example.com',
      password: 'password12',
      displayName: 'New',
    });

    expect(result.accessToken).toBe('access-token');
    expect(result.refreshToken).toBeDefined();
    expect(result.user.email).toBe('new@example.com');
    expect(prisma.refreshToken.create).toHaveBeenCalled();
  });

  it('login throws UnauthorizedException for unknown user', async () => {
    prisma.user.findUnique.mockResolvedValue(null);
    await expect(
      service.login({ email: 'x@y.com', password: 'password12' }),
    ).rejects.toBeInstanceOf(UnauthorizedException);
  });

  it('login throws UnauthorizedException for bad password', async () => {
    prisma.user.findUnique.mockResolvedValue({
      id: 'u1',
      email: 'a@b.com',
      passwordHash: 'hashed',
      displayName: 'A',
      role: 'USER',
      preferredLocale: 'ar',
    });
    (argon2.verify as jest.Mock).mockResolvedValue(false);

    await expect(
      service.login({ email: 'a@b.com', password: 'wrongpass1' }),
    ).rejects.toBeInstanceOf(UnauthorizedException);
  });
});
