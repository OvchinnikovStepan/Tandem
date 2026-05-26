import { useEffect } from "react";

import { useAppShellSlots } from "@/modules/AppShell";

export function useAppStubChrome() {
    const { clearTopBarContent, closeRightPanel } = useAppShellSlots();

    useEffect(() => {
        clearTopBarContent();
        closeRightPanel();
    }, [clearTopBarContent, closeRightPanel]);
}
