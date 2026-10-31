'use client';

import { FormEvent, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { register } from '@/lib/api';

export default function RegisterPage() {
  const router = useRouter();
  const [displayName, setDisplayName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await register({
        email,
        password,
        displayName,
        preferredLocale: 'ar',
      });
      localStorage.setItem('onehub_access', res.accessToken);
      localStorage.setItem('onehub_refresh', res.refreshToken);
      localStorage.setItem('onehub_user', JSON.stringify(res.user));
      router.push('/apps');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="mx-auto max-w-md">
      <h1 className="text-2xl font-bold text-slate-900 dark:text-white">إنشاء حساب</h1>
      <p className="mt-1 text-sm text-slate-500">حساب منصة OneHub فقط (لا يدمج حسابات التطبيقات)</p>

      <form onSubmit={onSubmit} className="mt-6 space-y-4 rounded-2xl border border-slate-200 bg-white p-6 dark:border-slate-800 dark:bg-slate-900">
        {error && (
          <div className="rounded-xl bg-red-50 px-3 py-2 text-sm text-red-700 dark:bg-red-950/40 dark:text-red-200">
            {error}
          </div>
        )}
        <label className="block text-sm">
          <span className="text-slate-600 dark:text-slate-300">الاسم</span>
          <input
            required
            minLength={2}
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            className="mt-1 w-full rounded-xl border border-slate-300 bg-white px-3 py-2 dark:border-slate-700 dark:bg-slate-950"
          />
        </label>
        <label className="block text-sm">
          <span className="text-slate-600 dark:text-slate-300">البريد</span>
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="mt-1 w-full rounded-xl border border-slate-300 bg-white px-3 py-2 dark:border-slate-700 dark:bg-slate-950"
          />
        </label>
        <label className="block text-sm">
          <span className="text-slate-600 dark:text-slate-300">كلمة المرور (8 أحرف على الأقل)</span>
          <input
            type="password"
            required
            minLength={8}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="mt-1 w-full rounded-xl border border-slate-300 bg-white px-3 py-2 dark:border-slate-700 dark:bg-slate-950"
          />
        </label>
        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-xl bg-brand-600 py-2.5 text-sm font-medium text-white hover:bg-brand-700 disabled:opacity-60"
        >
          {loading ? 'جاري التسجيل…' : 'تسجيل'}
        </button>
      </form>

      <p className="mt-4 text-center text-sm text-slate-500">
        لديك حساب؟{' '}
        <Link href="/login" className="font-medium">
          دخول
        </Link>
      </p>
    </div>
  );
}
