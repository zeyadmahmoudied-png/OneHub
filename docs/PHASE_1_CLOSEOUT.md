# Phase 1 — Foundation Closeout

**Repository:** https://github.com/zeyadmahmoudied-png/OneHub  
**Date:** 2026-10-31

## Exit criteria

| Criterion | Verification |
|-----------|----------------|
| Backend build + unit tests | CI job `backend` — no `--passWithNoTests` |
| Postgres migrate + smoke | CI Postgres 16 + `prisma migrate deploy` + `scripts/ci-smoke.mjs` |
| Web typecheck + build | CI job `web` |
| SDK build + tests | CI job `sdk` |
| Android `assembleDebug` + APK | CI job `android` + artifact `onehub-debug-apk` |
| Gradle Wrapper in Git | `gradlew`, `gradlew.bat`, `gradle-wrapper.jar`, properties (8.11.1) |
| Lockfiles + `npm ci` only | backend, web, sdk locks committed; CI fails if `npm ci` fails |

## Committed infrastructure

### Gradle Wrapper (official Gradle 8.11.1)

- `apps/android/gradlew`
- `apps/android/gradlew.bat`
- `apps/android/gradle/wrapper/gradle-wrapper.jar`
- `apps/android/gradle/wrapper/gradle-wrapper.properties` → `gradle-8.11.1-bin.zip`

CI verifies these files exist and does **not** download the wrapper at runtime.

### npm lockfiles

- `backend/package-lock.json`
- `apps/web/package-lock.json`
- `packages/sdk/package-lock.json`

## Reference runs

- Prior full green (APK ~11 MB): https://github.com/zeyadmahmoudied-png/OneHub/actions/runs/41235767261
- Latest Actions: https://github.com/zeyadmahmoudied-png/OneHub/actions

Confirm the newest `CI` workflow run on `main` is green after the lock/wrapper commits.

## Explicit non-goals

- Phase 2 **not started**
- All **23** advanced features remain in `docs/ROADMAP.md` only
- No Control Center source assumed or rewritten
- No APK-in-APK
