import { Search } from "lucide-react";

import { Input } from "@/ui/Input";

type ChatSearchBarProps = {
    value: string;
    onChange: (value: string) => void;
};

export function ChatSearchBar({ value, onChange }: ChatSearchBarProps) {
    return (
        <header className="min-h-[88px] flex items-center gap-3 border-b border-accent-gray bg-accent-white px-6 py-4">
            <div className="relative flex-1">
                <Search className="pointer-events-none absolute left-3 top-1/2 size-5 -translate-y-1/2 text-icon-gray" />
                <Input
                    value={value}
                    onChange={(event) => onChange(event.target.value)}
                    placeholder="Поиск ваших чатов..."
                    className="h-10 rounded-full pl-10 pr-4"
                />
            </div>
        </header>
    );
}
