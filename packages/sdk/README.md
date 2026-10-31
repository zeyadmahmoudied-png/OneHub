# @onehub/sdk

Foundation SDK for integrating independent apps with OneHub.

## Scope (Phase 1)

- `AppMetadata` definition
- `OneHubClient` for registry API
- Integration event shape
- Permission contracts

This does **not** mean existing apps are already connected. Adapters will be added per app later.

## Install (local)

```bash
cd packages/sdk
npm install
npm run build
```

## Example

```ts
import { OneHubClient, defineApp } from '@onehub/sdk';

const meta = defineApp({
  appId: 'my-plan',
  displayNameEn: 'My Plan',
  displayNameAr: 'خطتي',
});

const client = new OneHubClient({
  baseUrl: 'http://localhost:3000',
  accessToken: process.env.ONEHUB_TOKEN,
});

// await client.registerApp(meta); // requires DEVELOPER/ADMIN
const apps = await client.listApps();
```
