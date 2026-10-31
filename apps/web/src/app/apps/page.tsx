import Link from 'next/link';
import { fetchApps } from '@/lib/api';

export const dynamic = 'force-dynamic';

export default async function AppsPage() {
  let data;
  let error: string | null = null;

  try {
    data = await fetchApps();
  } catch (e) {
    error = e instanceof Error ? e.message : 'Failed to load apps';
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-red-200 bg-red-50 p-6 text-red-800 dark:border-red-900 dark:bg-red-950/40 dark:text-red-200">
        <h1 className="text-xl font-semibold">تعذر تحميل التطبيقات</h1>
        <p className="mt-2 text-sm">{error}</p>
        <p className="mt-2 text-sm opacity-80">
          تأكد أن الـ Backend يعمل على العنوان المحدد في NEXT_PUBLIC_API_URL.
        </p>
      </div>
    );
  }

  const items = data?.items ?? [];

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">التطبيقات</h1>
          <p className="mt-1 text-sm text-slate-500">
            {data?.total ?? 0} تطبيق مسجّل في المنصة
          </p>
        </div>
      </div>

      {items.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-300 p-10 text-center text-slate-500 dark:border-slate-700">
          لا توجد تطبيقات بعد. شغّل seed من الـ Backend.
        </div>
      ) : (
        <ul className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {items.map((app) => (
            <li key={app.appId}>
              <Link
                href={`/apps/${app.appId}`}
                className="block rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:border-brand-500 hover:shadow-md dark:border-slate-800 dark:bg-slate-900"
              >
                <div className="flex items-start justify-between gap-2">
                  <h2 className="font-semibold text-slate-900 dark:text-white">
                    {app.displayNameAr || app.displayNameEn}
                  </h2>
                  <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs text-slate-600 dark:bg-slate-800 dark:text-slate-300">
                    {app.status}
                  </span>
                </div>
                <p className="mt-1 text-xs text-slate-400">{app.appId}</p>
                <p className="mt-3 line-clamp-2 text-sm text-slate-600 dark:text-slate-400">
                  {app.descriptionAr || app.descriptionEn || '—'}
                </p>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
