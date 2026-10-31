/**
 * OneHub SDK — foundation for third-party / internal app integration.
 * Apps remain independent; this SDK only provides contracts and a thin API client.
 */

export type AdapterType = 'intent' | 'deeplink' | 'api' | 'sdk' | 'web';

export type AppStatus = 'planned' | 'registered' | 'active' | 'disabled';

export interface AppMetadata {
  appId: string;
  displayNameEn: string;
  displayNameAr: string;
  descriptionEn?: string;
  descriptionAr?: string;
  packageName?: string;
  webUrl?: string;
  iconUrl?: string;
  capabilities?: string[];
  adapterType?: AdapterType;
  status?: AppStatus;
  currentVersion?: string;
}

export interface PermissionContract {
  /** Capability required on source app */
  sourceCapability: string;
  /** Capability required on target app */
  targetCapability: string;
  /** Whether user consent is mandatory before the action */
  requiresUserConsent: boolean;
  /** Optional description */
  description?: string;
}

export type IntegrationEventType =
  | 'share.text'
  | 'share.image'
  | 'share.file'
  | 'launch'
  | 'sync.request'
  | 'custom';

export interface IntegrationEvent {
  type: IntegrationEventType;
  sourceAppId: string;
  targetAppId: string;
  payload?: Record<string, unknown>;
  timestamp?: string;
}

export interface OneHubClientOptions {
  baseUrl: string;
  accessToken?: string;
  fetchImpl?: typeof fetch;
}

export class OneHubClient {
  private baseUrl: string;
  private accessToken?: string;
  private fetchImpl: typeof fetch;

  constructor(options: OneHubClientOptions) {
    this.baseUrl = options.baseUrl.replace(/\/$/, '');
    this.accessToken = options.accessToken;
    this.fetchImpl = options.fetchImpl ?? fetch;
  }

  setAccessToken(token: string | undefined) {
    this.accessToken = token;
  }

  private headers(json = true): Record<string, string> {
    const h: Record<string, string> = {};
    if (json) h['Content-Type'] = 'application/json';
    if (this.accessToken) h['Authorization'] = `Bearer ${this.accessToken}`;
    return h;
  }

  async listApps(query?: { q?: string; status?: string }) {
    const sp = new URLSearchParams();
    if (query?.q) sp.set('q', query.q);
    if (query?.status) sp.set('status', query.status);
    const qs = sp.toString();
    const res = await this.fetchImpl(`${this.baseUrl}/apps${qs ? `?${qs}` : ''}`, {
      headers: this.headers(false),
    });
    if (!res.ok) throw new Error(`listApps failed: ${res.status}`);
    return res.json();
  }

  async getApp(appId: string) {
    const res = await this.fetchImpl(`${this.baseUrl}/apps/${encodeURIComponent(appId)}`, {
      headers: this.headers(false),
    });
    if (!res.ok) throw new Error(`getApp failed: ${res.status}`);
    return res.json();
  }

  async registerApp(meta: AppMetadata) {
    const res = await this.fetchImpl(`${this.baseUrl}/apps`, {
      method: 'POST',
      headers: this.headers(true),
      body: JSON.stringify(meta),
    });
    if (!res.ok) {
      const body = await res.text();
      throw new Error(`registerApp failed: ${res.status} ${body}`);
    }
    return res.json();
  }

  /**
   * Emit an integration event (stub for Phase 3 Integration Engine).
   * Currently validates the contract shape only; backend endpoint comes later.
   */
  createIntegrationEvent(event: IntegrationEvent): IntegrationEvent {
    if (!event.sourceAppId || !event.targetAppId || !event.type) {
      throw new Error('Invalid integration event');
    }
    return {
      ...event,
      timestamp: event.timestamp ?? new Date().toISOString(),
    };
  }
}

/** Built-in permission contracts for common actions */
export const DEFAULT_PERMISSION_CONTRACTS: PermissionContract[] = [
  {
    sourceCapability: 'share',
    targetCapability: 'receive_share',
    requiresUserConsent: true,
    description: 'Share text or media between apps',
  },
  {
    sourceCapability: 'launch',
    targetCapability: 'launchable',
    requiresUserConsent: false,
    description: 'Launch target app via Intent / deep link',
  },
];

export function defineApp(meta: AppMetadata): AppMetadata {
  if (!meta.appId || !meta.displayNameEn || !meta.displayNameAr) {
    throw new Error('appId, displayNameEn and displayNameAr are required');
  }
  return {
    adapterType: 'sdk',
    status: 'registered',
    capabilities: ['launch'],
    ...meta,
  };
}
