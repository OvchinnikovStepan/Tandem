import { useCallback, useEffect, useMemo, useState } from "react";

import type { SuggestedFriend } from "@/modules/Chats/types/chatTypes";

import { useAppShellSlots } from "@/modules/AppShell";
import {
    ChatConversation,
    ChatList,
    ChatSearchBar,
    useChatMessages,
    useChatsList,
} from "@/modules/Chats";
import { EmojiPickerPanel } from "@/modules/Chats/components/panels/EmojiPickerPanel";
import { SuggestedFriendsPanel } from "@/modules/Chats/components/panels/SuggestedFriendsPanel";

export default function Chats() {
    const {
        setTopBarContent,
        clearTopBarContent,
        setRightPanel,
        closeRightPanel,
    } = useAppShellSlots();
    const [search, setSearch] = useState("");
    const [rightPanelView, setRightPanelView] = useState<"friends" | "emoji">(
        "friends",
    );
    const [isRightPanelOpen, setIsRightPanelOpen] = useState(true);
    const {
        chats,
        isLoading: isChatsLoading,
        startChatWithFriend,
        refreshChats,
    } = useChatsList();

    const [activeChatId, setActiveChatId] = useState<string | undefined>();
    const {
        messages,
        isLoading: isMessagesLoading,
        error: messagesError,
        sendMessage,
    } = useChatMessages({ chatId: activeChatId });

    useEffect(() => {
        if (!isChatsLoading && chats.length && !activeChatId) {
            setActiveChatId(chats[0].id);
        }
    }, [activeChatId, chats, isChatsLoading]);

    useEffect(() => {
        setTopBarContent(<ChatSearchBar value={search} onChange={setSearch} />);
        return () => {
            clearTopBarContent();
            setSearch("");
        };
    }, [search, setTopBarContent, clearTopBarContent]);

    const handleStartChatWithFriend = useCallback(
        async (friend: SuggestedFriend) => {
            const chat = await startChatWithFriend(friend);
            setActiveChatId(chat.id);
            setIsRightPanelOpen(false);
            await refreshChats();
        },
        [refreshChats, startChatWithFriend],
    );

    useEffect(() => {
        const content =
            rightPanelView === "friends" ? (
                <SuggestedFriendsPanel
                    onStartChat={(friend) => {
                        void handleStartChatWithFriend(friend);
                    }}
                />
            ) : (
                <EmojiPickerPanel onClose={() => setIsRightPanelOpen(false)} />
            );

        setRightPanel({ isOpen: isRightPanelOpen, content });
    }, [
        handleStartChatWithFriend,
        isRightPanelOpen,
        rightPanelView,
        setRightPanel,
    ]);

    useEffect(() => {
        return () => {
            closeRightPanel();
        };
    }, [closeRightPanel]);

    const filteredChats = useMemo(() => {
        if (!search.trim()) return chats;

        const normalized = search.toLowerCase();

        return chats.filter((chat) =>
            chat.name.toLowerCase().includes(normalized),
        );
    }, [chats, search]);

    const handleSelectChat = (id: string) => {
        setActiveChatId(id);
        setIsRightPanelOpen(false);
    };

    const handleSendMessage = useCallback(
        async (text: string) => {
            await sendMessage(text);
            await refreshChats();
        },
        [refreshChats, sendMessage],
    );

    const handleToggleEmojiPanel = () => {
        if (isRightPanelOpen && rightPanelView === "emoji") {
            setIsRightPanelOpen(false);
            return;
        }
        setRightPanelView("emoji");
        setIsRightPanelOpen(true);
    };

    return (
        <div className="flex h-[calc(100vh-88px)] min-h-0 min-w-0 max-h-[calc(100vh-88px)] flex-1 overflow-hidden">
            <ChatList
                chats={filteredChats}
                activeChatId={activeChatId}
                onSelectChat={handleSelectChat}
            />
            <ChatConversation
                chat={chats.find((chat) => chat.id === activeChatId)}
                messages={messages}
                isLoading={isMessagesLoading}
                error={messagesError}
                onToggleEmojiPanel={handleToggleEmojiPanel}
                onSendMessage={handleSendMessage}
            />
        </div>
    );
}
