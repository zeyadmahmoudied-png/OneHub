# OneHub Roadmap

## Phase 0 — Audit & Architecture ✅ (in progress)

- [x] Requirements captured
- [x] Architecture documented
- [x] Tech stack chosen
- [x] ROADMAP.md, ARCHITECTURE.md, DATABASE_SCHEMA.md, SECURITY.md
- [ ] Full dependency map of managed apps (pending source access)

## Phase 1 — Foundation (current focus)

- [ ] Create repository structure (apps/android, apps/web, backend, packages/sdk)
- [ ] NestJS backend skeleton + Prisma schema (core tables)
- [ ] JWT auth (register / login / refresh)
- [ ] App Registry module (CRUD for registered apps)
- [ ] Next.js web skeleton (auth pages + Apps list)
- [ ] Android skeleton (Compose + Material 3 + navigation shells)
- [ ] GitHub Actions: lint + test + build placeholders
- [ ] Initial unit tests for App Registry & Auth

**Exit criteria**: Backend builds and tests pass; web builds; Android project opens and compiles; App Registry API works with real DB.

## Phase 2 — Multi-App Core

- Register the five known apps in the registry (metadata only)
- Per-app detail page (Overview, Versions, Health)
- Platform user profile + basic RBAC
- Launch via Intent / Deep Link from Android
- Ability to add a new app via API without code change to core

## Phase 3 — Integration Engine

- Integration SDK (Kotlin + TypeScript)
- File / text / image share flows with consent
- Event bus skeleton
- Conflict resolution policy document + basic implementation
- Integration Recipes table

## Phase 4 — Administration & Analytics

- Per-app admin sections (only when real APIs exist)
- Basic analytics events from registry
- Error & diagnostics endpoints
- Version / release metadata management

## Phase 5 — Developer Studio

- GitHub OAuth / PAT integration
- List repos, Actions runs, artifacts
- Release notes / changelog display
- Compatibility checker (static rules first)
- Dependency monitor (package.json / build.gradle parsing)

## Phase 6 — AI & Automation

- AI Command Center (permission-gated)
- Workflow Generator (stores workflows only)
- Troubleshooter (reads logs + suggests, never auto-fixes without confirmation)
- Personal Automation Hub

## Phase 7 — Advanced Features

Implement the remaining features from the original list only after lower phases are stable:

1. Universal Search
2. Workspace
3. Unified Inbox
4. Smart Clipboard
5. AI Command Center
6. AI Workflow Generator
7. AI Troubleshooter
8. Universal File Bridge
9. Integration Recipes
10. Developer Studio (advanced)
11. SDK Generator
12. Compatibility Checker
13. Release Pipeline
14. Dependency Monitor
15. Security Center (advanced)
16. Cost Center
17. App Health Score
18. Personal App Store
19. Digital Twin
20. What-if Simulator
21. Unified Backup & Recovery
22. App Sandbox
23. Personal Automation Hub

Each feature will receive its own design note and acceptance tests before implementation.

## Out of Scope Forever (unless requirements change)

- Embedding full third-party APKs inside OneHub process
- Automatic account merging across apps
- Storing third-party app business data inside OneHub database without explicit adapter contracts
