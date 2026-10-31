import Link from 'next/link';
import { fetchApp } from '@/lib/api';

export const dynamic = 'force-dynamic';

export default async function AppDetailPage({
  params,
}: {
  params: Promise<{ appId: string }>;
}) {
  const { appId } = await params;
  let app;
  let error: string | null = null;

  try {
    app = await fetchApp(appId);
  } catch (e) {
    error = e instanceof Error ? e.message : 'Not found';
  }

  if (error || !app) {
    return (
      <div className="space-y-4">
        <Link href="/apps" className="text-sm">
          ← العودة للتطبيقات
        </Link>
        <div className="rounded-2xl border border-red-200 bg-red-50 p-6 text-red-800 dark:border-red-900 dark:bg-red-950/40 dark:text-red-200">
          {error || 'التطبيق غير موجود'}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <Link href="/apps" className="text-sm text-slate-500 hover:text-brand-600">
        ← العودة للتطبيقات
      </Link>

      <div className="rounded-2xl border border-slate-200 bg-white p-6 dark:border-slate-800 dark:bg-slate-900">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 dark:text-white">
              {app.displayNameAr}
            </h1>
            <p className="text-slate-500">{app.displayNameEn}</p>
            <p className="mt-1 font-mono text-xs text-slate-400">{app.appId}</p>
          </div>
          <span className="rounded-full bg-slate-100 px-3 py-1 text-sm dark:bg-slate-800">
            {app.status}
          </span>
        </div>

        <p className="mt-4 text-slate-700 dark:text-slate-300">
          {app.descriptionAr || app.descriptionEn || 'لا يوجد وصف.'}
        </p>

        <dl className="mt-6 grid gap-3 text-sm sm:grid-cols-2">
          <div>
            <dt className="text-slate-500">نوع المحول</dt>
            <dd className="font-medium">{app.adapterType}</dd>
          </div>
          <div>
            <dt className="text-slate-500">الإصدار</dt>
            <dd className="font-medium">{app.currentVersion || '—'}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Package</dt>
            <dd className="font-mono text-xs">{app.packageName || '—'}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Health</dt>
            <dd className="font-medium">{app.healthScore}</dd>
          </div>
        </dl>
      </div>
    </div>
  );
}
