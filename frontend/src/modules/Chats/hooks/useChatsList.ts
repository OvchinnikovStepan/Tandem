import { useCallback, useEffect, useState } from "react";

import {
    getChatsRequest,
    startChatWithFriendRequest,
} from "@/modules/Chats/api/chatMessagesApi";
import type {
    ChatPreview,
    SuggestedFriend,
} from "@/modules/Chats/types/chatTypes";

export function useChatsList() {
    const [chats, setChats] = useState<ChatPreview[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let isMounted = true;

        const loadChats = async () => {
            setIsLoading(true);
            setError(null);
            try {
                const response = await getChatsRequest();
                if (!isMounted) return;
                setChats(response);
            } catch (requestError) {
                if (!isMounted) return;
                const message =
                    requestError instanceof Error
                        ? requestError.message
                        : "Не удалось загрузить чаты";
                setError(message);
            } finally {
                if (!isMounted) return;
                setIsLoading(false);
            }
        };

        void loadChats();

        return () => {
            isMounted = false;
        };
    }, []);

    const startChatWithFriend = useCallback(async (friend: SuggestedFriend) => {
        const chat = await startChatWithFriendRequest(friend);
        setChats((prev) => {
            if (prev.some((item) => item.id === chat.id)) {
                return prev;
            }
            return [chat, ...prev];
        });
        return chat;
    }, []);

    const refreshChats = useCallback(async () => {
        const response = await getChatsRequest();
        setChats(response);
    }, []);

    return {
        chats,
        isLoading,
        error,
        startChatWithFriend,
        refreshChats,
    };
}
