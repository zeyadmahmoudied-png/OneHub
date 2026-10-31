import Link from 'next/link';

export function SiteHeader() {
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
          <Link href="/login" className="text-slate-600 hover:text-slate-900 dark:text-slate-300 dark:hover:text-white">
            دخول
          </Link>
          <Link
            href="/register"
            className="rounded-xl bg-brand-600 px-3 py-1.5 font-medium text-white hover:bg-brand-700"
          >
            تسجيل
          </Link>
        </nav>
      </div>
    </header>
  );
}
