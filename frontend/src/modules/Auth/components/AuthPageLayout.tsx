import type { ReactNode } from "react";
import { Link } from "react-router";
import { ArrowLeft } from "lucide-react";

type AuthPageLayoutProps = {
    title: string;
    description: string;
    children: ReactNode;
    footer: ReactNode;
    backTo?: string;
};

export function AuthPageLayout({
    title,
    description,
    children,
    footer,
    backTo = "/",
}: AuthPageLayoutProps) {
    return (
        <div className="min-h-screen bg-landing-bg">
            <header className="border-b-2 border-header-border bg-accent-white">
                <div className="container flex h-18 items-center justify-between max-w-276">
                    <Link
                        to={backTo}
                        className="inline-flex items-center gap-2 text-sm font-roboto font-medium text-heading-black hover:opacity-80"
                    >
                        <ArrowLeft className="size-4" />
                        Назад
                    </Link>
                    <img
                        src="/logo/Black_Horiz_Logo.svg"
                        alt="Tandem"
                        className="h-8"
                        draggable="false"
                    />
                </div>
            </header>

            <main className="container grid min-h-[calc(100vh-72px)] max-w-276 items-center gap-8 py-8 lg:grid-cols-2">
                <section className="hidden rounded-default border-2 border-accent-gray bg-accent-white p-8 lg:block">
                    <h2 className="text-3xl font-roboto font-bold text-heading-black">
                        Tandem
                    </h2>
                    <p className="mt-4 text-base font-roboto text-base-black">
                        Корпоративный мессенджер для общения, дружбы и
                        совместных интересов.
                    </p>
                    <img
                        src="/hero/Hero_Image.png"
                        alt="Tandem app"
                        className="mx-auto mt-8 max-w-72"
                        draggable="false"
                    />
                </section>

                <section className="rounded-default border-2 border-accent-gray bg-accent-white p-6 sm:p-8">
                    <h1 className="text-3xl font-roboto font-bold text-heading-black sm:text-4xl">
                        {title}
                    </h1>
                    <p className="mt-2 text-sm font-roboto text-base-black">
                        {description}
                    </p>
                    <div className="mt-8">{children}</div>
                    <div className="mt-6 text-sm font-roboto text-base-black">
                        {footer}
                    </div>
                </section>
            </main>
        </div>
    );
}
