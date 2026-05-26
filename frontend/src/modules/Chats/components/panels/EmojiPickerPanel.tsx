import { X } from "lucide-react";

const EMOJI_SAMPLE = [
    "\u{1F600}",
    "\u{1F603}",
    "\u{1F606}",
    "\u{1F923}",
    "\u{1F642}",
    "\u{2764}",
    "\u{1F44D}",
    "\u{1F389}",
    "\u{1F37A}",
    "\u{2600}",
    "\u{1F3AE}",
    "\u{270C}",
];

const UI_TEXT = {
    title: "\u0421\u043c\u0430\u0439\u043b\u0438\u043a\u0438",
    close: "\u0417\u0430\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043d\u0435\u043b\u044c",
    insert: "\u0412\u0441\u0442\u0430\u0432\u0438\u0442\u044c",
};

type EmojiPickerPanelProps = {
    onClose: () => void;
};

export function EmojiPickerPanel({ onClose }: EmojiPickerPanelProps) {
    return (
        <aside className="flex h-full w-[312px] shrink-0 flex-col bg-accent-white shadow-side-lines">
            <header className="flex min-h-[88px] items-center gap-3 border-b border-accent-gray px-6 py-5">
                <h2 className="flex-1 text-base font-roboto font-bold text-heading-black">
                    {UI_TEXT.title}
                </h2>
                <button
                    type="button"
                    onClick={onClose}
                    className="flex size-10 items-center justify-center rounded-full text-icon-gray transition-colors hover:bg-landing-bg hover:text-heading-black"
                    aria-label={UI_TEXT.close}
                >
                    <X className="size-6" />
                </button>
            </header>

            <div className="flex flex-1 flex-col gap-2 overflow-y-auto px-4 py-4">
                <div className="grid grid-cols-6 gap-2">
                    {EMOJI_SAMPLE.map((emoji) => (
                        <button
                            key={emoji}
                            type="button"
                            className="flex aspect-square items-center justify-center rounded-xl text-xl transition-colors hover:bg-landing-bg"
                            aria-label={`${UI_TEXT.insert} ${emoji}`}
                        >
                            {emoji}
                        </button>
                    ))}
                </div>
            </div>
        </aside>
    );
}
