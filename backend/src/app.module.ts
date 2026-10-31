import { Module } from '@nestjs/common';
import { AppsModule } from './apps/apps.module';
import { PrismaModule } from './prisma/prisma.module';
import { HealthController } from './health.controller';

@Module({
  imports: [PrismaModule, AppsModule],
  controllers: [HealthController],
})
export class AppModule {}
