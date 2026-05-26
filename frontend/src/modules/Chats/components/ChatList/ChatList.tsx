import { Pin, VolumeX } from "lucide-react";
import { UserAvatar } from "@/components/UserAvatar";
import type { ChatPreview } from "@/modules/Chats/types/chatTypes";

type ChatListProps = {
    chats: ChatPreview[];
    activeChatId?: string;
    onSelectChat: (id: string) => void;
};

export function ChatList({ chats, activeChatId, onSelectChat }: ChatListProps) {
    return (
        <aside className="flex min-h-0 w-[300px] shrink-0 flex-col overflow-hidden border-r border-accent-gray bg-accent-white">
            <div className="flex items-center justify-between px-5 py-3">
                <h2 className="text-base font-roboto font-bold text-heading-black">
                    Чаты
                </h2>
            </div>

            <ul className="flex-1 overflow-y-auto pb-2 pt-1">
                {chats.map((chat) => {
                    const isActive = chat.id === activeChatId;

                    return (
                        <li key={chat.id}>
                            <button
                                type="button"
                                onClick={() => onSelectChat(chat.id)}
                                className={`flex w-full items-center gap-3 px-4 py-3 text-left transition-colors ${
                                    isActive
                                        ? "bg-landing-bg"
                                        : "hover:bg-landing-bg/70"
                                }`}
                            >
                                <UserAvatar name={chat.name} size="lg" />

                                <div className="min-w-0 flex-1">
                                    <div className="flex items-center gap-2">
                                        <p className="truncate text-sm font-roboto font-bold text-heading-black">
                                            {chat.name}
                                        </p>
                                        {chat.isPinned && (
                                            <Pin className="size-4 shrink-0 text-action-button-text" />
                                        )}
                                        {chat.isMuted && (
                                            <VolumeX className="size-4 shrink-0 text-action-button-text" />
                                        )}
                                    </div>
                                    <p className="mt-0.5 line-clamp-1 text-xs font-roboto text-base-black">
                                        {chat.lastMessage}
                                    </p>
                                </div>

                                <div className="flex flex-col items-end gap-1">
                                    <span className="text-[0.6875rem] font-roboto text-base-black">
                                        {chat.updatedAt}
                                    </span>
                                    {chat.unreadCount ? (
                                        <span className="flex min-w-5 items-center justify-center rounded-full border border-action-button-text/50 bg-action-button-text/10 px-1 text-[0.6875rem] font-roboto font-semibold text-action-button-text">
                                            {chat.unreadCount}
                                        </span>
                                    ) : null}
                                </div>
                            </button>
                        </li>
                    );
                })}
            </ul>
        </aside>
    );
}

export type { ChatPreview };
