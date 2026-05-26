import { Outlet } from "react-router";
import { useCallback, useMemo, useState, type ReactNode } from "react";

import { AppTopBar } from "@/modules/AppShell/components/AppTopBar/AppTopBar";
import { AppRightPanel } from "@/modules/AppShell/components/AppRightPanel/AppRightPanel";

export type AppShellSlotsContext = {
    setTopBarContent: (content: ReactNode | null) => void;
    clearTopBarContent: () => void;
    setRightPanel: (payload: {
        isOpen: boolean;
        content: ReactNode | null;
    }) => void;
    closeRightPanel: () => void;
};

type AppShellLayoutProps = {
    leftSidebar: ReactNode;
};

export function AppShellLayout({ leftSidebar }: AppShellLayoutProps) {
    const [topBarContent, setTopBarContent] = useState<ReactNode | null>(null);
    const [rightPanelContent, setRightPanelContent] =
        useState<ReactNode | null>(null);
    const [rightPanelOpen, setRightPanelOpen] = useState(false);

    const clearTopBarContent = useCallback(() => {
        setTopBarContent(null);
    }, []);

    const setRightPanel = useCallback(
        (payload: { isOpen: boolean; content: ReactNode | null }) => {
            setRightPanelOpen(payload.isOpen);
            setRightPanelContent(payload.content);
        },
        [],
    );

    const closeRightPanel = useCallback(() => {
        setRightPanelOpen(false);
    }, []);

    const contextValue = useMemo<AppShellSlotsContext>(
        () => ({
            setTopBarContent,
            clearTopBarContent,
            setRightPanel,
            closeRightPanel,
        }),
        [clearTopBarContent, setRightPanel, closeRightPanel],
    );

    return (
        <div className="min-h-screen bg-landing-bg">
            <div className="flex min-h-screen bg-accent-white shadow-default">
                {leftSidebar}

                <div className="flex min-h-0 min-w-0 flex-1 flex-col">
                    <AppTopBar content={topBarContent} />

                    <div className="flex min-h-0 flex-1">
                        <Outlet context={contextValue} />
                    </div>
                </div>

                <AppRightPanel
                    isOpen={rightPanelOpen}
                    content={rightPanelContent}
                />
            </div>
        </div>
    );
}
