/** Browser-only session helpers. Tokens never go into source control. */

import { AuthResponse, AuthUser } from './api';

const ACCESS = 'onehub_access';
const REFRESH = 'onehub_refresh';
const USER = 'onehub_user';

export function saveSession(res: AuthResponse) {
  if (typeof window === 'undefined') return;
  localStorage.setItem(ACCESS, res.accessToken);
  localStorage.setItem(REFRESH, res.refreshToken);
  localStorage.setItem(USER, JSON.stringify(res.user));
}

export function clearSession() {
  if (typeof window === 'undefined') return;
  localStorage.removeItem(ACCESS);
  localStorage.removeItem(REFRESH);
  localStorage.removeItem(USER);
}

export function getAccessToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(ACCESS);
}

export function getRefreshToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(REFRESH);
}

export function getStoredUser(): AuthUser | null {
  if (typeof window === 'undefined') return null;
  const raw = localStorage.getItem(USER);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthUser;
  } catch {
    return null;
  }
}
