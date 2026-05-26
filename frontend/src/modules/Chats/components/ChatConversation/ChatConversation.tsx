import {
    MoreHorizontal,
    Paperclip,
    Phone,
    SendHorizontal,
    Smile,
    Video,
} from "lucide-react";
import { useEffect, useRef, useState } from "react";

import { UserAvatar } from "@/components/UserAvatar";
import type { ChatMessage, ChatPreview } from "@/modules/Chats/types/chatTypes";

type ChatConversationProps = {
    chat?: ChatPreview;
    messages: ChatMessage[];
    isLoading: boolean;
    error: string | null;
    onToggleEmojiPanel: () => void;
    onSendMessage: (text: string) => Promise<void> | void;
};

const UI_TEXT = {
    empty: "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0447\u0430\u0442, \u0447\u0442\u043e\u0431\u044b \u043d\u0430\u0447\u0430\u0442\u044c \u043f\u0435\u0440\u0435\u043f\u0438\u0441\u043a\u0443",
    status: "\u0431\u044b\u043b(\u0430) \u0432 \u0441\u0435\u0442\u0438 \u043d\u0435\u0434\u0430\u0432\u043d\u043e",
    call: "\u041f\u043e\u0437\u0432\u043e\u043d\u0438\u0442\u044c",
    video: "\u0412\u0438\u0434\u0435\u043e\u0437\u0432\u043e\u043d\u043e\u043a",
    more: "\u0415\u0449\u0435",
    loading: "\u0417\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0439...",
    today: "\u0421\u0435\u0433\u043e\u0434\u043d\u044f",
    attach: "\u041f\u0440\u0438\u043a\u0440\u0435\u043f\u0438\u0442\u044c",
    placeholder: "\u0421\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435...",
    emoji: "\u042d\u043c\u043e\u0434\u0437\u0438",
    send: "\u041e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c",
};

const CHAT_HEADER_ICON_CLASS =
    "size-4 fill-heading-black stroke-heading-black text-heading-black transition-opacity duration-200 ease-in-out group-hover:opacity-60";

export function ChatConversation({
    chat,
    messages,
    isLoading,
    error,
    onToggleEmojiPanel,
    onSendMessage,
}: ChatConversationProps) {
    const [draft, setDraft] = useState("");
    const isSendingRef = useRef(false);

    useEffect(() => {
        setDraft("");
        isSendingRef.current = false;
    }, [chat?.id]);

    if (!chat) {
        return (
            <section className="flex flex-1 items-center justify-center bg-accent-white px-6">
                <p className="text-base font-roboto text-base-black">
                    {UI_TEXT.empty}
                </p>
            </section>
        );
    }

    const handleSend = async () => {
        const trimmed = draft.trim();
        if (!trimmed || isSendingRef.current) return;

        isSendingRef.current = true;
        try {
            await onSendMessage(trimmed);
            setDraft("");
        } finally {
            isSendingRef.current = false;
        }
    };

    return (
        <section className="flex min-h-0 min-w-0 flex-1 flex-col overflow-hidden bg-landing-bg">
            <header className="flex h-[72px] shrink-0 items-center justify-between border-b border-accent-gray bg-accent-white px-6">
                <div className="flex items-center gap-3">
                    <UserAvatar name={chat.name} size="lg" />
                    <div className="min-w-0">
                        <p className="truncate text-sm font-roboto font-bold text-heading-black">
                            {chat.name}
                        </p>
                        <p className="text-xs font-roboto text-base-black">
                            {UI_TEXT.status}
                        </p>
                    </div>
                </div>

                <div className="flex items-center gap-1">
                    <button
                        type="button"
                        className="group flex size-9 items-center justify-center rounded-full transition-colors hover:bg-landing-bg"
                        aria-label={UI_TEXT.call}
                    >
                        <Phone
                            className={CHAT_HEADER_ICON_CLASS}
                            strokeWidth={1.5}
                        />
                    </button>
                    <button
                        type="button"
                        className="group flex size-9 items-center justify-center rounded-full transition-colors hover:bg-landing-bg"
                        aria-label={UI_TEXT.video}
                    >
                        <Video
                            className={CHAT_HEADER_ICON_CLASS}
                            strokeWidth={1.5}
                        />
                    </button>
                    <button
                        type="button"
                        className="group flex size-9 items-center justify-center rounded-full transition-colors hover:bg-landing-bg"
                        aria-label={UI_TEXT.more}
                    >
                        <MoreHorizontal
                            className={CHAT_HEADER_ICON_CLASS}
                            strokeWidth={1.5}
                        />
                    </button>
                </div>
            </header>

            <div className="flex-1 overflow-y-auto px-3 py-5">
                {isLoading ? (
                    <p className="text-sm font-roboto text-base-black">
                        {UI_TEXT.loading}
                    </p>
                ) : null}
                {error ? (
                    <p className="text-sm font-roboto text-red-500">{error}</p>
                ) : null}
                {!isLoading && !error ? (
                    <div className="flex w-full flex-col gap-3">
                        <div className="my-1 text-center text-[11px] font-roboto text-base-black">
                            {UI_TEXT.today}
                        </div>
                        {messages.map((message) => (
                            <article
                                key={message.id}
                                className={`max-w-[30%] wrap-break-word rounded-2xl px-4 py-2 ${
                                    message.isMine
                                        ? "ml-auto rounded-br-md bg-[#e9edf2] text-heading-black"
                                        : "rounded-bl-md border border-accent-gray bg-accent-white text-heading-black"
                                }`}
                            >
                                {message.text ? (
                                    <p className="text-[13px] font-roboto leading-relaxed">
                                        {message.text}
                                    </p>
                                ) : null}
                                {message.attachment?.type === "image" ? (
                                    <div className="mt-1 overflow-hidden rounded-xl border border-accent-gray bg-white">
                                        {message.attachment.imageUrl ? (
                                            <img
                                                src={message.attachment.imageUrl}
                                                alt={message.attachment.label}
                                                className="max-h-64 w-full object-cover"
                                            />
                                        ) : (
                                            <div className="flex h-36 items-center justify-center p-3 text-sm font-roboto text-heading-black">
                                                {message.attachment.label}
                                            </div>
                                        )}
                                    </div>
                                ) : null}
                                <p className="mt-1 text-right text-[10px] font-roboto text-base-black/80">
                                    {message.sentAt}
                                </p>
                            </article>
                        ))}
                    </div>
                ) : null}
            </div>

            <footer className="border-t border-accent-gray bg-accent-white px-3 py-3">
                <div className="flex w-full items-center gap-2 rounded-3xl border border-accent-gray bg-accent-white px-3 py-2 shadow-default">
                    <button
                        type="button"
                        className="flex size-8 shrink-0 items-center justify-center rounded-full text-action-button-text transition-colors hover:bg-header-button hover:text-action-button-text"
                        aria-label={UI_TEXT.attach}
                    >
                        <Paperclip className="size-4" />
                    </button>
                    <input
                        type="text"
                        value={draft}
                        onChange={(event) => setDraft(event.target.value)}
                        placeholder={UI_TEXT.placeholder}
                        className="h-8 flex-1 border-none bg-transparent text-[13px] font-roboto text-heading-black outline-none placeholder:text-base-black/80"
                        onKeyDown={(event) => {
                            if (event.key === "Enter") {
                                event.preventDefault();
                                void handleSend();
                            }
                        }}
                    />
                    <button
                        type="button"
                        onClick={onToggleEmojiPanel}
                        className="flex size-8 shrink-0 items-center justify-center rounded-full text-action-button-text transition-colors hover:bg-header-button hover:text-action-button-text"
                        aria-label={UI_TEXT.emoji}
                    >
                        <Smile className="size-4" />
                    </button>
                    <button
                        type="button"
                        onClick={() => void handleSend()}
                        disabled={!draft.trim()}
                        className="flex size-8 shrink-0 items-center justify-center rounded-full bg-header-button text-action-button-text transition-colors hover:bg-header-button-hover disabled:opacity-50"
                        aria-label={UI_TEXT.send}
                    >
                        <SendHorizontal className="size-4" />
                    </button>
                </div>
            </footer>
        </section>
    );
}
