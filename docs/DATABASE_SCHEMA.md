# OneHub Database Schema (Phase 1)

PostgreSQL + Prisma. Only platform metadata is stored here. Application business data remains in each app.

## Core Tables

### users
Platform identity only.

| Column          | Type         | Notes                          |
|-----------------|--------------|--------------------------------|
| id              | uuid PK      |                                |
| email           | text unique  |                                |
| password_hash   | text         | argon2                         |
| display_name    | text         |                                |
| preferred_locale| text         | `ar` / `en`                    |
| created_at      | timestamptz  |                                |
| updated_at      | timestamptz  |                                |

### refresh_tokens
| Column      | Type        | Notes                |
|-------------|-------------|----------------------|
| id          | uuid PK     |                      |
| user_id     | uuid FK     |                      |
| token_hash  | text        |                      |
| expires_at  | timestamptz |                      |
| revoked_at  | timestamptz | nullable             |
| created_at  | timestamptz |                      |

### apps (App Registry)
| Column            | Type        | Notes                                      |
|-------------------|-------------|--------------------------------------------|
| id                | uuid PK     |                                            |
| app_id            | text unique | stable slug e.g. `my-plan`                 |
| display_name_en   | text        |                                            |
| display_name_ar   | text        |                                            |
| description_en    | text        |                                            |
| description_ar    | text        |                                            |
| package_name      | text        | Android package (nullable)                 |
| web_url           | text        | official web entry (nullable)              |
| icon_url          | text        |                                            |
| capabilities      | jsonb       | array of strings                           |
| adapter_type      | text        | `intent` / `deeplink` / `api` / `sdk` / `web` |
| status            | text        | `planned` / `registered` / `active` / `disabled` |
| current_version   | text        |                                            |
| health_score      | int         | 0-100, later                               |
| created_at        | timestamptz |                                            |
| updated_at        | timestamptz |                                            |

### app_versions
| Column      | Type        | Notes                |
|-------------|-------------|----------------------|
| id          | uuid PK     |                      |
| app_id      | uuid FK     |                      |
| version     | text        |                      |
| changelog   | text        |                      |
| released_at | timestamptz |                      |
| artifact_url| text        | nullable             |

### integration_logs
Audit of cross-app actions.

| Column        | Type        | Notes                          |
|---------------|-------------|--------------------------------|
| id            | uuid PK     |                                |
| source_app_id | text        |                                |
| target_app_id | text        |                                |
| action        | text        |                                |
| user_id       | uuid FK     |                                |
| status        | text        | success / denied / error       |
| metadata      | jsonb       |                                |
| created_at    | timestamptz |                                |

### audit_logs
Platform-level security events.

| Column     | Type        | Notes                |
|------------|-------------|----------------------|
| id         | uuid PK     |                      |
| user_id    | uuid        | nullable             |
| action     | text        |                      |
| resource   | text        |                      |
| ip         | text        |                      |
| user_agent | text        |                      |
| metadata   | jsonb       |                      |
| created_at | timestamptz |                      |

## Future Tables (not in Phase 1)

- `roles`, `permissions`, `user_roles` (platform RBAC)
- `integration_recipes`
- `workflows`
- `health_checks`
- `cost_entries`
- `backups`

## Isolation Notes

- No foreign keys from OneHub tables into any third-party app database.
- Capabilities and consent are checked at the Integration Engine layer, not only by DB constraints.
