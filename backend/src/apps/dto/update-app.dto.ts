import { IsString, IsOptional, IsArray, IsIn, IsInt, Min, Max, MaxLength } from 'class-validator';

export class UpdateAppDto {
  @IsOptional()
  @IsString()
  @MaxLength(128)
  displayNameEn?: string;

  @IsOptional()
  @IsString()
  @MaxLength(128)
  displayNameAr?: string;

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

  @IsOptional()
  @IsInt()
  @Min(0)
  @Max(100)
  healthScore?: number;
}
