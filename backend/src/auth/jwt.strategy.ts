import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { ConfigService } from '@nestjs/config';
import { PrismaService } from '../prisma/prisma.service';

export type JwtPayload = {
  sub: string;
  email: string;
  role: string;
};

function resolveAccessSecret(config: ConfigService): string {
  const secret = config.get<string>('JWT_ACCESS_SECRET');
  if (!secret || secret.length < 32) {
    if (process.env.NODE_ENV === 'production') {
      throw new Error('JWT_ACCESS_SECRET must be set (min 32 chars) in production');
    }
    return 'dev-only-access-secret-min-32-chars!!';
  }
  return secret;
}

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(
    config: ConfigService,
    private readonly prisma: PrismaService,
  ) {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: resolveAccessSecret(config),
    });
  }

  async validate(payload: JwtPayload) {
    const user = await this.prisma.user.findUnique({
      where: { id: payload.sub },
      select: {
        id: true,
        email: true,
        displayName: true,
        role: true,
        preferredLocale: true,
      },
    });
    if (!user) {
      throw new UnauthorizedException();
    }
    return user;
  }
}
