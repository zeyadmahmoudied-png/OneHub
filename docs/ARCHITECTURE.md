# OneHub Architecture

## 1. Goals

- Single entry point for discovering, launching, monitoring and integrating independent apps.
- Strict data and permission isolation between apps.
- Ability to register any future app (including Control Center) without platform rewrite.
- Support Android client + Web dashboard + shared backend.
- Low operational cost, free-tier friendly where possible.

## 2. High-Level Diagram (logical)

```
┌─────────────────────────────────────────────────────────────┐
│                        Clients                               │
│  ┌──────────────────┐          ┌──────────────────────────┐ │
│  │  Android App     │          │  Web Dashboard (Next.js) │ │
│  │  (Compose + M3)  │          │  Admin + User            │ │
│  └────────┬─────────┘          └────────────┬─────────────┘ │
└───────────┼─────────────────────────────────┼───────────────┘
            │ HTTPS / JWT                     │
            ▼                                 ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (NestJS)                      │
│  Auth | App Registry | Integration Engine | Analytics ...   │
└────────────────────────────┬────────────────────────────────┘
                             │
            ┌────────────────┼────────────────┐
            ▼                ▼                ▼
     ┌────────────┐   ┌────────────┐   ┌────────────┐
     │ PostgreSQL │   │  Object    │   │  Event Bus │
     │ (Prisma)   │   │  Storage   │   │ (later)    │
     └────────────┘   └────────────┘   └────────────┘

Independent Apps (outside OneHub process)
┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐
│ My Plan    │ │PhotoCleaner│ │ ZOZ Creator│ │ ...        │
│ (own data) │ │ (own data) │ │ (own data) │ │            │
└─────┬──────┘ └─────┬──────┘ └─────┬──────┘ └─────┬──────┘
      │              │              │              │
      └──────────────┴──────────────┴──────────────┘
                     │
         Integration via SDK / Adapter / Intents / APIs
```

## 3. Key Components

### 3.1 App Registry
Central catalog of known applications.
Each entry stores:
- `appId` (stable string)
- display name, icons, package name / web URL
- supported capabilities (launch, share, files, analytics, premium…)
- current version metadata
- health status
- adapter type (Intent, DeepLink, HTTP API, SDK, Web Embed)

### 3.2 Integration Engine
- Receives requests from OneHub clients or from apps via SDK.
- Validates permissions of source and target.
- Executes allowed actions (share text/image, file transfer, event publish).
- Never bypasses app-level consent.

### 3.3 Authentication & Authorization
- Platform-level identity (OneHub account).
- Per-app roles and permissions remain owned by each app.
- OneHub never automatically elevates or merges app accounts.
- JWT short-lived + refresh tokens. Secrets only on backend.

### 3.4 Developer Studio (phased)
Uses official GitHub APIs to list repos, Actions, artifacts, releases.
Requires explicit user-granted GitHub OAuth / PAT with minimal scopes.

### 3.5 Future AI Layer
Sits behind the same permission system. Can only call documented APIs and must request confirmation for sensitive actions.

## 4. Integration Strategies (no APK embedding)

| Method              | When to use                                      |
|---------------------|--------------------------------------------------|
| Android Intent      | Launch app, share content, open specific screen  |
| App Links / Deep Link | Precise navigation into an app                 |
| HTTP API + SDK      | Data exchange, events, file metadata             |
| Embedded WebView    | Official web UI of an app when no native entry   |
| Content Provider / FileProvider | Secure file sharing between apps        |

## 5. Data Isolation Rules

- OneHub database stores only platform metadata, registry, integration logs, platform users.
- Application business data stays in each app’s own storage / backend.
- Any shared data must be explicitly consented and scoped.

## 6. Extensibility Path for Control Center

1. Register `control-center` in App Registry with its package name / URLs.
2. Implement or generate an Adapter that exposes the capabilities Control Center supports.
3. Map existing Control Center functions to Integration Recipes where useful.
4. No rewrite of Control Center source is required; only an adapter layer.

## 7. Non-Goals (Phase 0–1)

- Running arbitrary APKs inside OneHub process.
- Automatic merging of user accounts across apps.
- Implementing all 23 advanced features at once.
