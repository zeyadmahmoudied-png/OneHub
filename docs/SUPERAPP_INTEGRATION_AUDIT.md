# Super App Integration — Phase 1 Repository Audit

**Date:** 2026-11-01  
**Scope:** Inventory only. **No feature modules copied, no code merged.**  
**Host repo:** https://github.com/zeyadmahmoudied-png/OneHub

## 1. OneHub (host) current state

| Area | Status |
|------|--------|
| Android app | Single module `apps/android` · package `com.onehub.app` · minSdk 26 · compile/target 35 · Compose BOM 2024.12.01 · Navigation Compose |
| Structure | Flat: `MainActivity`, `ui/Screens`, `data/*` — **not yet multi-module features/** |
| Backend | NestJS Auth + App Registry (optional for Super App shell; not required for local feature modules) |
| CI | Backend / Web / SDK / Android assembleDebug green |
| Access Control | **Not implemented** (Owner & Permission Code System is Phase 5) |

**Implication:** Phase 2 must introduce `settings.gradle.kts` multi-module layout and a shell navigation graph without breaking Foundation CI.

---

## 2. Per-app inventory

### 2.1 PhotoCleaner — **AVAILABLE · PRIMARY MERGE CANDIDATE**

| Field | Value |
|-------|--------|
| Repo | https://github.com/zeyadmahmoudied-png/PhotoCleaner |
| Package / applicationId | `com.photocleaner.app` |
| Stack | Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room + KSP, Coil, WorkManager, DataStore |
| AGP / Kotlin | AGP 8.7.2 · Kotlin 2.0.21 · Compose plugin · compileSdk 35 · minSdk 26 |
| Architecture | domain (usecases) / data (Room) / presentation (screens + ViewModel) |
| Permissions | `READ_MEDIA_IMAGES`, legacy storage |
| Network | **None required** for core photo work |
| DB | Room `AppDatabase` — must keep **isolated name** when embedded |
| UI completeness | Home, Scan, other screens present; some features marked skeleton/TBD in README |
| Unit tests | `GroupProtectionUseCaseTest`, `SafeDeleteUseCaseTest` |
| Integration method | Convert `app` → **Android Library** feature module; host provides single Application + merged permissions |
| Risks | Resource name clashes; Room DB name; Application subclass (`PhotoCleanerApp`); KSP must be enabled on host |

### 2.2 ZOZ Creator (AutoYouTube-Android.) — **AVAILABLE (private repo)**

| Field | Value |
|-------|--------|
| Repo | https://github.com/zeyadmahmoudied-png/AutoYouTube-Android. *(trailing dot; private)* |
| Package / applicationId | `com.autoyoutube.app` |
| Path | `android/` subproject |
| Stack | Kotlin + Compose BOM 2024.12.01 · MediaCodec/MediaMuxer local MP4 · DataStore · DocumentFile |
| Network | **None** (standalone; README denies WebView/FastAPI) |
| Completeness | Image→video core present; audio/voice/effects still incomplete per README |
| Integration method | Library module from `android/app`; MediaStore/write permissions merge |
| Risks | Heavy media pipeline; storage paths `Movies/ZoZ`; package rename to feature namespace |

### 2.3 YouTube Control Center — **AVAILABLE · PARTIAL / FRAGILE**

| Field | Value |
|-------|--------|
| Repo | https://github.com/zeyadmahmoudied-png/Youtubecontrolcenter |
| Package / applicationId | `com.youtubecc.app` |
| Stack | Kotlin + Compose · Retrofit/Gson · Coil · DataStore · **YouTube Data API** |
| Compose toolchain | Older BOM 2024.02 + `composeOptions.kotlinCompilerExtensionVersion = 1.5.8` — **conflicts** with OneHub/PhotoCleaner Kotlin 2.x compose plugin style |
| Secrets | `YOUTUBE_API_KEY` via BuildConfig placeholder — **must not ship real key in APK** |
| Network | **Required** (INTERNET) |
| Structure | Large `MainActivity.kt` (~15KB) holding much UI; limited package split |
| README claim | States source not fully uploaded; **tree actually contains source** |
| Integration method | Library + dependency alignment (Compose/Kotlin versions); API key via local properties / future server proxy |
| Risks | Version conflict; API quota; OAuth redirect scheme `com.youtubecc.app://oauth2redirect` |

### 2.4 My Plan — **NOT AVAILABLE (empty repos)**

| Candidate | Result |
|-----------|--------|
| https://github.com/yzyw75437-crypto/My-Plan | Empty repository (409 empty) |
| https://github.com/zeyadmahmoudied-png/My-Plan | Empty |
| https://github.com/zeyadmahmoudied-png/My-Plan- | Private — **not audited** (need access confirmation / non-empty content) |

**Action required from owner:** Provide a non-empty repo URL or grant access + confirm which branch holds the real My Plan source.

### 2.5 Dar Al Bayan — **NOT FOUND**

No repository under `zeyadmahmoudied-png` or `yzyw75437-crypto` matches «Dar Al Bayan / دار البيان». Public search hits unrelated projects (e.g. desktop Albayan).

**Action required:** Provide GitHub URL (or confirm it is private under another account).

### 2.6 YouTube Creator (distinct from ZOZ and Control Center)

| Candidate | Result |
|-----------|--------|
| https://github.com/zeyadmahmoudied-png/Youtube | Empty |
| Control Center | Separate product (analytics) — **must not be mixed with ZOZ** |
| ZOZ | Video creation — different scope |

**Action required:** Clarify whether «YouTube Creator» is another repo, or if Control Center is the intended fifth app.

---

## 3. Gradle / dependency conflict matrix

| Concern | OneHub | PhotoCleaner | ZOZ | YT Control Center |
|---------|--------|--------------|-----|-------------------|
| minSdk | 26 | 26 | 26 | 26 |
| compileSdk | 35 | 35 | 35 | 35 |
| Kotlin | (via AGP/compose plugin) | 2.0.21 | compose plugin | older 1.5.8 compiler ext |
| Compose BOM | 2024.12.01 | 2024.10.01 | 2024.12.01 | 2024.02.00 |
| KSP / Room | no | yes | no | no |
| Retrofit | yes (Moshi) | no | no | yes (Gson) |
| Application class | default | PhotoCleanerApp | TBD | YouTubeCCApplication |

**Plan:** Unify on Kotlin 2.x compose plugin + single Compose BOM (prefer 2024.12.01) in host; feature modules as `com.android.library`.

---

## 4. Integration strategy (planned — not executed)

Recommended host layout after Phase 2:

```
apps/android/
  settings.gradle.kts          # include features
  app/                         # shell only: launcher, nav host, access control UI
  core/                        # shared theme, result types
  navigation/
  features/
    photocleaner/              # library from PhotoCleaner sources
    zozcreator/                # library from AutoYouTube android/app
    youtubecc/                 # library from Youtubecontrolcenter (after alignment)
    myplan/                    # blocked until source exists
    daralbayan/                # blocked until source exists
  accesscontrol/               # Owner & Permission Code System (Phase 5)
```

**Method order (per rules):**  
1) Library modules reusing Compose UI + domain + local DB  
2) No APK-in-APK, no external Intents to original apps  
3) WebView only if a future app is purely web (none of the available three are)

**First real merge (Phase 3):** **PhotoCleaner** — richest structure, offline, tests, closest stack to OneHub.

---

## 5. Access Control (design note only — Phase 5)

- Local Owner Admin bootstrap (hashed identity, not hard-coded password in APK)
- Permission codes stored as **hashes**; grants for capabilities (hub, workspace, admin tools, …)
- Expiry / revoke / renew / max uses; auto-strip on expiry
- Central `AccessControl` gate used by navigation
- Document: local mode is trialable; **server-side enforcement required** against rooted/modified clients later
- Must be schema-ready for Backend without redesign

---

## 6. Blockers before Phase 2 code

1. **My Plan** source URL / access  
2. **Dar Al Bayan** source URL / access  
3. Confirm identity of **YouTube Creator** vs Control Center  
4. Optional: make `AutoYouTube-Android.` publicly readable for CI if CI must fetch it (otherwise vendor sources into OneHub under license ownership)

---

## 7. Explicit non-actions this phase

- No files copied from app repos into OneHub  
- No Gradle multi-module created yet  
- No Access Control implementation  
- No Phase 2–5 feature work  
- Original app repositories untouched  
