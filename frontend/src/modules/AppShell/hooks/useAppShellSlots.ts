import { useOutletContext } from "react-router";

import type { AppShellSlotsContext } from "@/modules/AppShell/components/AppShellLayout/AppShellLayout";

export function useAppShellSlots() {
    return useOutletContext<AppShellSlotsContext>();
}
