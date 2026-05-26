import { useAppStubChrome } from "@/modules/AppShell";

export default function Feed() {
    useAppStubChrome();

    return (
        <div className="flex min-h-0 flex-1 flex-col overflow-y-auto bg-accent-white p-8">
            <h1 className="text-2xl font-bold text-heading-black">Главная</h1>
            <p className="mt-2 text-base font-roboto text-base-black">
                Лента (заглушка). Правая панель свёрнута.
            </p>
        </div>
    );
}
