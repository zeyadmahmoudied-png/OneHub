import { strict as assert } from 'node:assert';
import { test } from 'node:test';
import { defineApp, OneHubClient } from './index';

test('defineApp requires ids and names', () => {
  assert.throws(() => defineApp({ appId: '', displayNameEn: 'A', displayNameAr: 'ب' } as any));
  const meta = defineApp({
    appId: 'my-plan',
    displayNameEn: 'My Plan',
    displayNameAr: 'خطتي',
  });
  assert.equal(meta.appId, 'my-plan');
  assert.equal(meta.adapterType, 'sdk');
  assert.equal(meta.status, 'registered');
});

test('createIntegrationEvent validates and stamps timestamp', () => {
  const client = new OneHubClient({ baseUrl: 'http://localhost:3000' });
  assert.throws(() =>
    client.createIntegrationEvent({
      type: 'launch',
      sourceAppId: '',
      targetAppId: 'x',
    }),
  );
  const ev = client.createIntegrationEvent({
    type: 'share.text',
    sourceAppId: 'a',
    targetAppId: 'b',
    payload: { text: 'hi' },
  });
  assert.equal(ev.sourceAppId, 'a');
  assert.ok(ev.timestamp);
});

test('listApps fails on non-ok response', async () => {
  const client = new OneHubClient({
    baseUrl: 'http://example.invalid',
    fetchImpl: (async () =>
      new Response('nope', { status: 500 })) as unknown as typeof fetch,
  });
  await assert.rejects(() => client.listApps());
});
