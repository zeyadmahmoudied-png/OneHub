# Phase 1 Closeout

## Intended exit criteria

| Criterion | How verified |
|-----------|----------------|
| Backend build | GitHub Actions job `backend` |
| Backend unit tests (Auth + Apps) | `npm test` in CI — must not use `--passWithNoTests` |
| Postgres migrate + smoke | CI service `postgres` + `prisma migrate deploy` + `scripts/ci-smoke.mjs` |
| Web typecheck + build | CI job `web` |
| SDK build + tests | CI job `sdk` (`node --test` after `tsc`) |
| Android Debug APK | CI job `android` + artifact `onehub-debug-apk` |

## CI URL

https://github.com/zeyadmahmoudied-png/OneHub/actions

After the closeout commits land on `main`, open the latest workflow run. **Do not treat this document as proof of a green run** — confirm the Actions UI.

## Known gaps still open at closeout time (agent environment)

1. **`package-lock.json` not committed** for backend / web / sdk — CI falls back to `npm install`. Generate locks locally with `npm install` and commit them when network allows.
2. **`gradle-wrapper.jar` / `gradlew` may be missing on remote** — CI job generates wrapper via `gradle/actions/setup-gradle` when `gradlew` is absent. Prefer committing the full wrapper for fully offline local builds.
3. **Agent could not finish long `npm install` / local Android SDK builds** due to environment limits; verification is delegated to GitHub Actions.

## Roadmap

All 23 advanced features remain listed in `docs/ROADMAP.md`. Phase 2 is **not** started by this closeout.
