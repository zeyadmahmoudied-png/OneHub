# OneHub

**Unified platform for managing, operating, and integrating independent applications.**

OneHub is a multi-app control plane that keeps every application fully independent while providing a single place to discover, launch, monitor, integrate, and administer them.

> Designed from day one so a future **Control Center** (or any other independent app) can be registered and integrated without rewriting existing code.

## Core Principles

1. **App Independence** — Each app keeps its own data, users, roles, settings, premium system, backups, and release cycle.
2. **No APK-in-APK** — Integration uses Android Intents, App Links, deep links, APIs, SDK, and embedded WebViews where appropriate.
3. **Secure by default** — No secrets in client code. Strict RBAC, data isolation, audit logs.
4. **Extensible** — New apps are added via App Registry + Adapter/SDK without rebuilding the platform.
5. **Real functionality only** — No fake data, no non-working buttons.

## Managed Applications (Registry)

| App ID              | Display Name          | Status     |
|---------------------|-----------------------|------------|
| `my-plan`           | My Plan               | Planned    |
| `photo-cleaner`     | PhotoCleaner          | Planned    |
| `zoz-creator`       | ZOZ Creator           | Planned    |
| `dar-al-bayan`      | Dar Al Bayan          | Planned    |
| `youtube-creator`   | YouTube Creator       | Planned    |
| `control-center`    | Control Center        | Future     |

## Platform Sections

- **Home** — Overview & quick actions
- **Apps** — App registry & launch
- **Workspace** — Unified working area
- **Integration Center** — Cross-app actions & recipes
- **AI Assistant** — Command center (phased)
- **Analytics** — Cross-app insights (permission-aware)
- **Developer Studio** — Builds, releases, SDK, compatibility
- **Security Center** — Auth, roles, audit, isolation
- **Settings** — Platform preferences

## Tech Stack (Phase 1)

| Layer          | Choice                          | Reason                                      |
|----------------|---------------------------------|---------------------------------------------|
| Android        | Kotlin + Jetpack Compose + M3   | Modern, official, excellent RTL support     |
| Web Dashboard  | Next.js 15 + Tailwind + shadcn  | Fast, SSR-ready, low cost                   |
| Backend        | NestJS + Prisma                 | Type-safe, modular, easy to scale           |
| Database       | PostgreSQL                      | Strong isolation, RLS-ready, migrations     |
| Auth           | JWT + Refresh Tokens            | No paid dependency, full control            |
| CI             | GitHub Actions                  | Free for public repos                       |
| Monorepo       | Simple folders + pnpm workspaces| Clear boundaries, easy CI                   |

## Repository Structure

```
OneHub/
├── apps/
│   ├── android/          # Kotlin + Compose client
│   └── web/              # Next.js admin & user dashboard
├── backend/              # NestJS API + Prisma
├── packages/
│   └── sdk/              # Integration SDK for third-party apps
├── docs/
│   ├── ARCHITECTURE.md
│   ├── ROADMAP.md
│   ├── DATABASE_SCHEMA.md
│   └── SECURITY.md
├── .github/workflows/
└── README.md
```

## Getting Started

See `docs/ROADMAP.md` and individual package READMEs after Phase 1 lands.

## License

Private / TBD by owner.
