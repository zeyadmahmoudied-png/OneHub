import {
  Controller,
  Get,
  Post,
  Patch,
  Delete,
  Param,
  Body,
  Query,
  HttpCode,
  UseGuards,
} from '@nestjs/common';
import { AppsService } from './apps.service';
import { CreateAppDto } from './dto/create-app.dto';
import { UpdateAppDto } from './dto/update-app.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { RolesGuard } from '../auth/roles.guard';
import { Roles } from '../auth/roles.decorator';

@Controller('apps')
export class AppsController {
  constructor(private readonly appsService: AppsService) {}

  /** Public read of the registry */
  @Get()
  findAll(
    @Query('status') status?: string,
    @Query('q') q?: string,
    @Query('page') page?: string,
    @Query('limit') limit?: string,
  ) {
    return this.appsService.findAll({
      status,
      q,
      page: page ? Number(page) : 1,
      limit: limit ? Number(limit) : 50,
    });
  }

  @Get(':appId')
  findOne(@Param('appId') appId: string) {
    return this.appsService.findByAppId(appId);
  }

  @Post()
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN', 'DEVELOPER')
  create(@Body() dto: CreateAppDto) {
    return this.appsService.create(dto);
  }

  @Patch(':appId')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN', 'DEVELOPER')
  update(@Param('appId') appId: string, @Body() dto: UpdateAppDto) {
    return this.appsService.update(appId, dto);
  }

  @Delete(':appId')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN')
  @HttpCode(204)
  async remove(@Param('appId') appId: string) {
    await this.appsService.remove(appId);
  }

  @Post('seed')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN')
  seed() {
    return this.appsService.seedKnownApps();
  }
}
