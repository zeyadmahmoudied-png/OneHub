# Phase 2 — Integration Foundation

**Date:** 2026-11-01  
**Scope decision:** Super App currently targets **three** apps only:

1. My Plan  
2. PhotoCleaner  
3. Dar Al Bayan  

ZOZ Creator / YouTube Control Center are **deferred** and removed from active slots.

**No real app source was merged in this phase.**

## Architecture after Phase 2

```
apps/android/
  app/                 # shell: MainActivity, Foundation API screens
  core/                # theme, FeatureSlot contracts
  navigation/          # Routes + OneHubNavHost (internal only)
  accesscontrol/       # Owner, codes, grants, engine + unit tests
  features/
    slots/             # honest PENDING_SOURCE UI for the three apps
```

```
settings.gradle.kts includes:
  :app :core :navigation :accesscontrol :features:slots
```

## Access Control Foundation

| Capability | Status |
|------------|--------|
| Owner provision (local password hash + salt) | Implemented (engine + in-memory repo) |
| Permission definitions (hub + feature capabilities) | Implemented |
| Issue codes (hashed storage, max uses, validity window) | Implemented |
| Activate / bind to user | Implemented |
| Expire grants automatically | Implemented |
| Revoke / renew | Implemented |
| Audit log (local list) | Implemented |
| Block ADMIN via normal code | Implemented + tested |
| Backend-ready `AccessRepository` interface | Implemented |
| Persistent DataStore backend | **Not yet** (in-memory for Phase 2 verification) |
| Server-side enforcement | **Documented as required later** |

Security note (shown in UI): local AC is not a substitute for server verification against a modified client.

## Navigation

- Internal Compose Navigation only (no external app Intents).
- Routes for hub registry, feature slots, owner setup/login/admin, redeem code.
- Feature detail keeps single-top style navigation back to shell.

## Source availability notes

| App | GitHub | Grok Library (this agent environment) |
|-----|--------|----------------------------------------|
| PhotoCleaner | Available | N/A |
| My Plan | Empty / private candidates | **Not found** in sandbox paths |
| Dar Al Bayan | No public repo under owner accounts | **Not found** in sandbox paths |

Phase 3 will start with **PhotoCleaner** (confirmed GitHub source). My Plan / Dar Al Bayan require the owner to surface library archives into a reachable path or repo before merge.

## Explicit non-goals this phase

- No PhotoCleaner / My Plan / Dar Al Bayan code copied into features
- No claim of integrated app functionality
- No Backend deploy requirement
- No changes to Nest/Web/SDK contracts beyond Android CI test steps

## CI expectations

- `:accesscontrol:testDebugUnitTest`
- `:app:assembleDebug`
- `:app:assembleRelease`
- Existing backend / web / sdk jobs unchanged
