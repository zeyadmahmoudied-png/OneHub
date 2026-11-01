import { Controller, Get, ServiceUnavailableException } from '@nestjs/common';
import { PrismaService } from './prisma/prisma.service';

@Controller('health')
export class HealthController {
  constructor(private readonly prisma: PrismaService) {}

  /** Liveness — process is up (no DB required). */
  @Get()
  check() {
    return {
      status: 'ok',
      service: 'onehub-api',
      timestamp: new Date().toISOString(),
    };
  }

  /** Readiness — DB must respond. Use for deploy probes. */
  @Get('ready')
  async ready() {
    try {
      await this.prisma.$queryRaw`SELECT 1`;
      return {
        status: 'ready',
        database: 'up',
        service: 'onehub-api',
        timestamp: new Date().toISOString(),
      };
    } catch {
      throw new ServiceUnavailableException({
        status: 'not_ready',
        database: 'down',
        service: 'onehub-api',
      });
    }
  }
}
