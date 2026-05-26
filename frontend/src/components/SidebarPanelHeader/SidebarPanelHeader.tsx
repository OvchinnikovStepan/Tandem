import { Bell, Settings } from "lucide-react";
import { useAtomValue } from "jotai";
import { authAtom } from "@/modules/Auth";
import { UserAvatar } from "@/components/UserAvatar";

type SidebarPanelHeaderProps = {
    fallbackName?: string;
};

export function SidebarPanelHeader({
    fallbackName = "Azunyan U. Wu",
}: SidebarPanelHeaderProps) {
    const auth = useAtomValue(authAtom);
    const displayName =
        auth.user?.name && auth.user.onboardingCompleted
            ? auth.user.name
            : fallbackName;

    return (
        <header className="flex min-h-[88px] items-center gap-3 border-b border-accent-gray px-6 py-5">
            <UserAvatar name={displayName} size="md" />

            <div className="ml-auto flex items-center gap-2">
                <button
                    type="button"
                    className="flex size-10 items-center justify-center rounded-full bg-header-button text-heading-black transition-colors hover:bg-header-button-hover"
                    aria-label="Уведомления"
                >
                    <Bell className="size-6" />
                </button>
                <button
                    type="button"
                    className="flex size-10 items-center justify-center rounded-full bg-header-button text-heading-black transition-colors hover:bg-header-button-hover"
                    aria-label="Настройки"
                >
                    <Settings className="size-6" />
                </button>
            </div>
        </header>
    );
}
