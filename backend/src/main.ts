import { NestFactory } from '@nestjs/core';
import { ValidationPipe, Logger } from '@nestjs/common';
import { AppModule } from './app.module';

async function bootstrap() {
  const logger = new Logger('Bootstrap');
  const isProd = process.env.NODE_ENV === 'production';

  if (isProd) {
    const access = process.env.JWT_ACCESS_SECRET;
    const refresh = process.env.JWT_REFRESH_SECRET;
    if (!access || access.length < 32 || !refresh || refresh.length < 32) {
      throw new Error(
        'Production requires JWT_ACCESS_SECRET and JWT_REFRESH_SECRET (min 32 chars each)',
      );
    }
    if (!process.env.DATABASE_URL) {
      throw new Error('Production requires DATABASE_URL');
    }
  }

  const app = await NestFactory.create(AppModule, {
    logger: isProd ? ['error', 'warn', 'log'] : ['error', 'warn', 'log', 'debug'],
  });

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
    }),
  );

  const corsOrigin = process.env.CORS_ORIGIN;
  app.enableCors({
    origin: corsOrigin ? corsOrigin.split(',').map((s) => s.trim()) : isProd ? false : true,
    credentials: true,
  });

  const port = Number(process.env.PORT) || 3000;
  await app.listen(port);
  logger.log(`OneHub API listening on port ${port} (env=${process.env.NODE_ENV || 'development'})`);
}
bootstrap();
