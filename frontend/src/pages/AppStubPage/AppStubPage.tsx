import { useAppStubChrome } from "@/modules/AppShell";

type AppStubPageProps = {
    title: string;
    description?: string;
};

export function AppStubPage({ title, description }: AppStubPageProps) {
    useAppStubChrome();

    return (
        <div className="flex min-h-0 flex-1 flex-col overflow-y-auto bg-accent-white p-8">
            <h1 className="text-2xl font-bold text-heading-black">{title}</h1>
            {description ? (
                <p className="mt-2 text-base font-roboto text-base-black">
                    {description}
                </p>
            ) : null}
        </div>
    );
}
