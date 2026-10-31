# OneHub Security Baseline

## 1. Principles

- Least privilege
- Defense in depth
- No secrets in client code or public repositories
- Explicit consent for every cross-app action
- Audit everything sensitive

## 2. Authentication

- Email + password (argon2id)
- Short-lived access JWT (15 min)
- Long-lived refresh tokens stored hashed, rotatable and revocable
- Refresh tokens bound to device/session when possible
- Future: optional OAuth providers, 2FA

## 3. Authorization

- Platform RBAC (admin, developer, user) — introduced in Phase 2+
- Per-app permissions remain the responsibility of each application
- OneHub never silently elevates privileges inside another app
- Integration Engine checks both platform permission and app consent before any transfer

## 4. Data Isolation

- OneHub database contains only platform metadata
- Business data of My Plan, PhotoCleaner, etc. never lands in OneHub DB unless an explicit, versioned adapter contract exists
- File shares use Android FileProvider / content URIs with temporary grants or signed short-lived URLs on the backend

## 5. Secrets Management

- All API keys, DB credentials, JWT secrets live in environment variables or a secrets manager
- `.env` files are gitignored
- CI uses GitHub Secrets
- Mobile app and web frontend receive only public configuration

## 6. Transport & Storage

- TLS everywhere in production
- Passwords hashed with argon2id
- Sensitive fields at rest encrypted when required (future)
- Database migrations are versioned and reviewed

## 7. Audit

- `audit_logs` table for login, logout, role changes, registry changes
- `integration_logs` for every cross-app action (success or denial)
- Logs retained according to policy (to be defined)

## 8. Client Hardening

- Certificate pinning evaluation for Android (Phase 4+)
- No debug logs with tokens in production builds
- Web: secure cookies / SameSite, CSP headers

## 9. Threat Model (initial)

| Threat                        | Mitigation                                      |
|-------------------------------|-------------------------------------------------|
| Stolen JWT                    | Short expiry + refresh rotation                 |
| Cross-app data leak           | Explicit consent + capability checks            |
| Supply-chain (dependencies)   | Lockfiles + Dependabot later                    |
| Insider privilege escalation  | RBAC + audit logs                               |
| Secret leakage in APK         | No secrets compiled into client                 |

## 10. Future Control Center Integration

When Control Center is registered, it will be treated exactly like any other app:
- Own auth boundary
- Own data store
- Adapter only exposes what Control Center explicitly allows
