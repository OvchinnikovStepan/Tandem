import type { ReactNode } from "react";

type AppTopBarProps = {
    content: ReactNode | null;
};

export function AppTopBar({ content }: AppTopBarProps) {
    if (content) return <>{content}</>;

    return (
        <header className="min-h-[88px] shrink-0 border-b border-accent-gray bg-accent-white" />
    );
}
