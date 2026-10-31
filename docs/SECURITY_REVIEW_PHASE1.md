# Phase 1 Security Review

## Verified controls

| Control | Status |
|---------|--------|
| Passwords hashed with Argon2 | Yes |
| Refresh tokens stored as SHA-256 hashes only | Yes |
| Refresh rotation + revoke on logout | Yes |
| JWT access short-lived (15m) | Yes |
| Mutations on apps require JWT + role | Yes |
| ValidationPipe whitelist | Yes |
| Throttler on auth routes | Yes |
| No secrets committed in repo | Yes (.env.example only) |
| JWT secret required in production (min 32) | Yes |

## Residual risks (documented, not blockers for Phase 1 close)

1. **Dev JWT fallback** — non-production allows a fixed dev secret if env missing. Operators must set strong secrets before any production deploy.
2. **Cleartext HTTP on Android** — `usesCleartextTraffic=true` for emulator/local only; must be disabled for release builds.
3. **CORS** — defaults permissive when `CORS_ORIGIN` unset; set explicit origins in production.
4. **No 2FA / email verification** yet — acceptable for foundation; planned later.
5. **Client stores tokens in localStorage (web)** — XSS risk; HttpOnly cookies preferred in a later hardening pass.
6. **Rate limits are in-memory** — per-instance only; needs Redis-backed limiter for multi-instance production.

## Logging

Auth service does not log passwords or raw tokens. Keep it that way.
