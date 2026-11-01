# Phase 2 — Integration Foundation (closeout)

**Date:** 2026-11-01  
**CI green:** https://github.com/zeyadmahmoudied-png/OneHub/actions/runs/41288965460

## Product scope (owner decision)

Active Super App targets:

1. **My Plan** — source archive received (`MyPlan-v2.23-polish-source.zip`)
2. **PhotoCleaner** — GitHub source available
3. **Dar Al Bayan** — UI pack archive received (`dar-al-bayan-ui-redesign-source.zip`)

Deferred: ZOZ Creator, YouTube Control Center.

**No real app source was merged into feature modules in Phase 2.**

See `docs/SOURCE_INVENTORY_3APPS.md`.

## Architecture

```
apps/android/
  app/                 # shell: MainActivity, Foundation API UI, Access UI
  core/                # OneHubTheme, FeatureSlot contracts (3 apps)
  navigation/          # Routes + OneHubNavHost (internal only)
  accesscontrol/       # pure Kotlin JVM: engine + repo + unit tests
  features/
    slots/             # honest PENDING_SOURCE list/detail screens
```

## Access Control

| Capability | Status |
|------------|--------|
| Owner provision (local SHA-256 + salt) | Done |
| Capability permissions (hub + feature.*) | Done |
| Issue / activate / revoke / renew codes | Done |
| Max uses + validity window | Done |
| Block ADMIN via code | Done + tested |
| Local audit events | Done |
| `AccessRepository` for future Backend | Done |
| In-memory store for Phase 2 verification | Done |
| DataStore persistence | Not yet |
| Server-side enforcement | Required later (documented in UI) |

**Unit tests:** 7 JVM tests in `:accesscontrol:test` (local + CI).

## Navigation

Internal Compose navigation only. Routes cover hub registry, feature slots, owner setup/login/admin, redeem code.

## CI results (run 41288965460)

| Job | Result |
|-----|--------|
| Backend + Postgres | Success |
| Web typecheck/build | Success |
| SDK build/tests | Success |
| Access control JVM tests | Success |
| assembleDebug | Success |
| assembleRelease | Success |

Artifacts:

- `onehub-debug-apk` — 11.1 MB
- `onehub-release-apk` — 7.62 MB

## Explicit non-goals completed as non-goals

- No PhotoCleaner / My Plan / Dar Al Bayan code inside `features/*`
- No external app Intents
- No Backend deploy
- No Phase 3 merge started

## Next (after review)

Phase 3: integrate **PhotoCleaner** first as a real feature module, then My Plan (Java/Views) and Dar Al Bayan (Capacitor/Web) with isolation strategies from the inventory doc.
