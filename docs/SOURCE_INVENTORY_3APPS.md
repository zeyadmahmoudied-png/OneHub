# Source inventory — scoped Super App apps

**Phase 2 does not merge these sources into feature modules.**  
Inventory only, after owner attached archives on 2026-11-01.

## 1. My Plan — `MyPlan-v2.23-polish-source.zip`

| Item | Value |
|------|--------|
| Package | `com.myplan.app` |
| Version | 2.23-polish (versionCode 34) |
| Language | **Java** (not Kotlin/Compose) |
| UI | Classic Android Views + large `MainActivity.java` / `Planner.java` |
| compileSdk / targetSdk | 34 |
| minSdk | 23 |
| Backend | Supabase (URL + anon key via `local.properties` / BuildConfig) |
| Firebase | Optional when `google-services.json` present |
| Notable modules | AccountAuth, Planner, alarms/widgets, PDF viewer, local backup, force-update |

**Integration implication (Phase 3+):** embedding requires either (a) gradual extraction of domain logic into an Android library module with View interop inside Compose host, or (b) a dedicated Activity hosted inside the OneHub task with a clear back path. Full rewrite to Compose is **out of scope** for first merge.

## 2. PhotoCleaner

| Item | Value |
|------|--------|
| GitHub | https://github.com/zeyadmahmoudied-png/PhotoCleaner |
| Status | Public repo available; **first merge candidate** after Phase 2 CI is green |

## 3. Dar Al Bayan — `dar-al-bayan-ui-redesign-source.zip`

| Item | Value |
|------|--------|
| Archive content | `dab-ui-pack/` — **React 19 + Vite + TypeScript + Capacitor 6** |
| appId | `com.daralbayan.quran` |
| Nature | Hybrid Web UI pack (not a full native Android Studio project in this zip) |
| Domains | students, teachers, groups, payments, attendance, reports, local DB, sync outbox |

**Integration implication:** best first path is WebView / Capacitor host module inside OneHub **or** publishing the web build as an embedded asset, after confirming offline DB and permission model. Native Kotlin rewrite is not implied by this archive.

## Phase 2 rule reminder

- Slots list only these three apps.
- Status remains `PENDING_SOURCE` until a real feature module lands.
- No copy of zip contents into `features/*` in Phase 2.
