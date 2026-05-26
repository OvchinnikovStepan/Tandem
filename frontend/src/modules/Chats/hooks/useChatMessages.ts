import { useCallback, useEffect, useRef, useState } from "react";

import {
    appendChatMessageRequest,
    getChatMessagesRequest,
} from "@/modules/Chats/api/chatMessagesApi";
import type { ChatMessage } from "@/modules/Chats/types/chatTypes";

type UseChatMessagesOptions = {
    chatId?: string;
};

export function useChatMessages({ chatId }: UseChatMessagesOptions) {
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const isSendingRef = useRef(false);
    const loadVersionRef = useRef(0);

    useEffect(() => {
        if (!chatId) {
            setMessages([]);
            setIsLoading(false);
            setError(null);
            return;
        }

        const loadVersion = ++loadVersionRef.current;
        let isMounted = true;

        const loadMessages = async () => {
            setIsLoading(true);
            setError(null);
            try {
                const response = await getChatMessagesRequest(chatId);
                if (!isMounted || loadVersion !== loadVersionRef.current) {
                    return;
                }
                setMessages(response);
            } catch (requestError) {
                if (!isMounted || loadVersion !== loadVersionRef.current) {
                    return;
                }
                const message =
                    requestError instanceof Error
                        ? requestError.message
                        : "Не удалось загрузить сообщения";
                setError(message);
            } finally {
                if (!isMounted || loadVersion !== loadVersionRef.current) {
                    return;
                }
                setIsLoading(false);
            }
        };

        void loadMessages();

        return () => {
            isMounted = false;
        };
    }, [chatId]);

    const sendMessage = useCallback(
        async (text: string) => {
            if (!chatId || isSendingRef.current) return;

            const trimmed = text.trim();
            if (!trimmed) return;

            isSendingRef.current = true;
            try {
                const newMessage = await appendChatMessageRequest(
                    chatId,
                    trimmed,
                );
                setMessages((prev) => {
                    if (prev.some((message) => message.id === newMessage.id)) {
                        return prev;
                    }
                    return [...prev, newMessage];
                });
            } finally {
                isSendingRef.current = false;
            }
        },
        [chatId],
    );

    return {
        messages,
        isLoading,
        error,
        sendMessage,
    };
}
