'use client';

import Link from 'next/link';
import { useEffect, useState } from 'react';
import { logout as apiLogout } from '@/lib/api';
import { clearSession, getRefreshToken, getStoredUser } from '@/lib/session';

export function SiteHeader() {
  const [name, setName] = useState<string | null>(null);

  useEffect(() => {
    const u = getStoredUser();
    setName(u?.displayName ?? null);
  }, []);

  async function onLogout() {
    const refresh = getRefreshToken();
    try {
      if (refresh) await apiLogout(refresh);
    } catch {
      // clear local even if server unreachable
    }
    clearSession();
    setName(null);
    window.location.href = '/';
  }

  return (
    <header className="border-b border-slate-200 bg-white/80 backdrop-blur dark:border-slate-800 dark:bg-slate-950/80">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
        <Link href="/" className="text-lg font-semibold tracking-tight text-slate-900 dark:text-white">
          OneHub
        </Link>
        <nav className="flex items-center gap-4 text-sm">
          <Link href="/apps" className="text-slate-600 hover:text-slate-900 dark:text-slate-300 dark:hover:text-white">
            التطبيقات
          </Link>
          {name ? (
            <>
              <span className="text-slate-500 dark:text-slate-400">{name}</span>
              <button
                type="button"
                onClick={onLogout}
                className="rounded-xl border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-50 dark:border-slate-700 dark:text-slate-200 dark:hover:bg-slate-900"
              >
                خروج
              </button>
            </>
          ) : (
            <>
              <Link href="/login" className="text-slate-600 hover:text-slate-900 dark:text-slate-300 dark:hover:text-white">
                دخول
              </Link>
              <Link
                href="/register"
                className="rounded-xl bg-brand-600 px-3 py-1.5 font-medium text-white hover:bg-brand-700"
              >
                تسجيل
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
