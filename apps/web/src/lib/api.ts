const API_BASE =
  process.env.NEXT_PUBLIC_API_URL?.replace(/\/$/, '') || 'http://localhost:3000';

export type AppItem = {
  id: string;
  appId: string;
  displayNameEn: string;
  displayNameAr: string;
  descriptionEn?: string | null;
  descriptionAr?: string | null;
  packageName?: string | null;
  webUrl?: string | null;
  iconUrl?: string | null;
  capabilities: string[] | unknown;
  adapterType: string;
  status: string;
  currentVersion?: string | null;
  healthScore: number;
};

export type AppsListResponse = {
  items: AppItem[];
  total: number;
  page: number;
  limit: number;
  totalPages: number;
};

export type AuthUser = {
  id: string;
  email: string;
  displayName: string;
  role: string;
  preferredLocale: string;
};

export type AuthResponse = {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: AuthUser;
};

async function parseError(res: Response): Promise<string> {
  try {
    const body = await res.json();
    if (Array.isArray(body?.message)) return body.message.join(', ');
    return body?.message || res.statusText || 'Request failed';
  } catch {
    return res.statusText || 'Request failed';
  }
}

export async function fetchApps(params?: {
  q?: string;
  status?: string;
}): Promise<AppsListResponse> {
  const sp = new URLSearchParams();
  if (params?.q) sp.set('q', params.q);
  if (params?.status) sp.set('status', params.status);
  const qs = sp.toString();
  const res = await fetch(`${API_BASE}/apps${qs ? `?${qs}` : ''}`, {
    cache: 'no-store',
  });
  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}

export async function fetchApp(appId: string): Promise<AppItem> {
  const res = await fetch(`${API_BASE}/apps/${encodeURIComponent(appId)}`, {
    cache: 'no-store',
  });
  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}

export async function register(input: {
  email: string;
  password: string;
  displayName: string;
  preferredLocale?: string;
}): Promise<AuthResponse> {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input),
  });
  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}

export async function fetchMe(accessToken: string): Promise<AuthUser> {
  const res = await fetch(`${API_BASE}/auth/me`, {
    headers: { Authorization: `Bearer ${accessToken}` },
    cache: 'no-store',
  });
  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}

export { API_BASE };
