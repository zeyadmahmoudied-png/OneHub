import Link from 'next/link';

export default function HomePage() {
  return (
    <div className="space-y-10">
      <section className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm dark:border-slate-800 dark:bg-slate-900">
        <h1 className="text-3xl font-bold tracking-tight text-slate-900 dark:text-white">
          منصة OneHub
        </h1>
        <p className="mt-3 max-w-2xl text-slate-600 dark:text-slate-300">
          منصة موحدة لإدارة وتشغيل وربط تطبيقاتك المستقلة مع الحفاظ على عزل
          البيانات والصلاحيات لكل تطبيق.
        </p>
        <div className="mt-6 flex flex-wrap gap-3">
          <Link
            href="/apps"
            className="rounded-xl bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700"
          >
            عرض التطبيقات
          </Link>
          <Link
            href="/login"
            className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 dark:border-slate-700 dark:text-slate-200 dark:hover:bg-slate-800"
          >
            تسجيل الدخول
          </Link>
        </div>
      </section>

      <section className="grid gap-4 sm:grid-cols-3">
        {[
          { title: 'استقلالية التطبيقات', body: 'كل تطبيق يحتفظ ببياناته ومستخدمينه وإصداراته.' },
          { title: 'التكامل الآمن', body: 'Intents وAPIs وSDK دون تضمين APK داخل APK.' },
          { title: 'قابل للتوسع', body: 'تسجيل تطبيقات جديدة عبر App Registry دون إعادة بناء المنصة.' },
        ].map((card) => (
          <div
            key={card.title}
            className="rounded-2xl border border-slate-200 bg-white p-5 dark:border-slate-800 dark:bg-slate-900"
          >
            <h2 className="font-semibold text-slate-900 dark:text-white">{card.title}</h2>
            <p className="mt-2 text-sm text-slate-600 dark:text-slate-400">{card.body}</p>
          </div>
        ))}
      </section>
    </div>
  );
}
