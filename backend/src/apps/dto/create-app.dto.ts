import { IsString, IsOptional, IsArray, IsIn, MaxLength } from 'class-validator';

export class CreateAppDto {
  @IsString()
  @MaxLength(64)
  appId: string;

  @IsString()
  @MaxLength(128)
  displayNameEn: string;

  @IsString()
  @MaxLength(128)
  displayNameAr: string;

  @IsOptional()
  @IsString()
  descriptionEn?: string;

  @IsOptional()
  @IsString()
  descriptionAr?: string;

  @IsOptional()
  @IsString()
  packageName?: string;

  @IsOptional()
  @IsString()
  webUrl?: string;

  @IsOptional()
  @IsString()
  iconUrl?: string;

  @IsOptional()
  @IsArray()
  capabilities?: string[];

  @IsOptional()
  @IsIn(['intent', 'deeplink', 'api', 'sdk', 'web'])
  adapterType?: string;

  @IsOptional()
  @IsIn(['planned', 'registered', 'active', 'disabled'])
  status?: string;

  @IsOptional()
  @IsString()
  currentVersion?: string;
}
