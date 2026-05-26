import type { ReactNode } from "react";

import { cn } from "@/lib/utils";

type AppRightPanelProps = {
    isOpen: boolean;
    content: ReactNode | null;
};

export function AppRightPanel({ isOpen, content }: AppRightPanelProps) {
    return (
        <div
            className={cn(
                "shrink-0 overflow-hidden transition-[width] duration-200 ease-out",
                isOpen ? "w-[312px]" : "w-0",
            )}
        >
            {isOpen ? (
                <div className="flex h-full min-h-0 w-[312px]">{content}</div>
            ) : null}
        </div>
    );
}
