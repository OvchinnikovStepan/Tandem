import { useEffect } from "react";

import { useAppShellSlots } from "@/modules/AppShell";
import { SuggestedFriendsPanel } from "@/modules/Chats/components/panels/SuggestedFriendsPanel";

export default function Friends() {
    const { clearTopBarContent, setRightPanel, closeRightPanel } =
        useAppShellSlots();

    useEffect(() => {
        clearTopBarContent();
        setRightPanel({ isOpen: true, content: <SuggestedFriendsPanel /> });
        return () => {
            closeRightPanel();
        };
    }, [clearTopBarContent, setRightPanel, closeRightPanel]);

    return (
        <div className="flex min-h-0 flex-1 flex-col overflow-y-auto bg-accent-white p-8">
            <h1 className="text-2xl font-bold text-heading-black">Друзья</h1>
            <p className="mt-2 max-w-xl text-base font-roboto text-base-black">
                Основной контент страницы. Справа — панель «Возможные друзья».
            </p>
        </div>
    );
}
