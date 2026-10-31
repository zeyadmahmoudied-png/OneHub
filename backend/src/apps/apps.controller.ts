import { Controller, Get, Post, Patch, Delete, Param, Body, HttpCode } from '@nestjs/common';
import { AppsService } from './apps.service';
import { CreateAppDto } from './dto/create-app.dto';
import { UpdateAppDto } from './dto/update-app.dto';

@Controller('apps')
export class AppsController {
  constructor(private readonly appsService: AppsService) {}

  @Get()
  findAll() {
    return this.appsService.findAll();
  }

  @Get(':appId')
  findOne(@Param('appId') appId: string) {
    return this.appsService.findByAppId(appId);
  }

  @Post()
  create(@Body() dto: CreateAppDto) {
    return this.appsService.create(dto);
  }

  @Patch(':appId')
  update(@Param('appId') appId: string, @Body() dto: UpdateAppDto) {
    return this.appsService.update(appId, dto);
  }

  @Delete(':appId')
  @HttpCode(204)
  async remove(@Param('appId') appId: string) {
    await this.appsService.remove(appId);
  }

  @Post('seed')
  seed() {
    return this.appsService.seedKnownApps();
  }
}
