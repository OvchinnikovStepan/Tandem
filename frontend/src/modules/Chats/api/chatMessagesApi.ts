import type {
    ChatMessage,
    ChatPreview,
    SuggestedFriend,
} from "@/modules/Chats/types/chatTypes";

const MOCK_DELAY = 250;

let MOCK_CHATS: ChatPreview[] = [
    {
        id: "1",
        name: "George Alan",
        lastMessage: "Вы же сможете отправить их c СДЭКом?",
        updatedAt: "21:00",
        unreadCount: 3,
    },
    {
        id: "2",
        name: "Safiya Fareena",
        lastMessage: "Видела, отвечу чуть позже",
        updatedAt: "19:21",
        unreadCount: 1,
        isPinned: true,
    },
    {
        id: "3",
        name: "Robert Allen",
        lastMessage: "Спасибо за понимание. Дай знать, если...",
        updatedAt: "19:07",
    },
    {
        id: "4",
        name: "Scott Franklin",
        lastMessage: "Отправлю голосовое через 10 минут",
        updatedAt: "17:32",
        isMuted: true,
    },
];

const MOCK_MESSAGES_BY_CHAT_ID: Record<string, ChatMessage[]> = {
    "1": [
        {
            id: "m-1",
            chatId: "1",
            senderName: "George Alan",
            isMine: true,
            text: "Здравствуйте, вы еще продаете эти часы?",
            sentAt: "10:48",
        },
        {
            id: "m-2",
            chatId: "1",
            senderName: "George Alan",
            isMine: false,
            text: "Да, продаю",
            sentAt: "10:57",
        },
        {
            id: "m-3",
            chatId: "1",
            senderName: "George Alan",
            isMine: true,
            text: "А можете еще раз отправить их фото?",
            sentAt: "11:03",
        },
        {
            id: "m-4",
            chatId: "1",
            senderName: "George Alan",
            isMine: false,
            text: "Конечно! Подождите пару минут",
            sentAt: "11:08",
        },
        {
            id: "m-5",
            chatId: "1",
            senderName: "George Alan",
            isMine: false,
            sentAt: "11:14",
            attachment: {
                type: "image",
                label: "Фото часов",
                imageUrl: "/chat/watch.png",
            },
        },
        {
            id: "m-6",
            chatId: "1",
            senderName: "George Alan",
            isMine: true,
            text: "Спасибо, выглядят отлично",
            sentAt: "11:17",
        },
        {
            id: "m-7",
            chatId: "1",
            senderName: "George Alan",
            isMine: true,
            text: "Вы же сможете отправить их СДЭКом?",
            sentAt: "11:19",
        },
    ],
    "2": [
        {
            id: "m-8",
            chatId: "2",
            senderName: "Safiya Fareena",
            isMine: false,
            text: "Привет! Вечером обсудим детали по проекту?",
            sentAt: "19:00",
        },
    ],
};

function getCurrentTimeLabel() {
    return new Intl.DateTimeFormat("ru-RU", {
        hour: "2-digit",
        minute: "2-digit",
    }).format(new Date());
}

function wait() {
    return new Promise((resolve) => {
        setTimeout(resolve, MOCK_DELAY);
    });
}

export async function getChatsRequest(): Promise<ChatPreview[]> {
    await wait();
    return MOCK_CHATS;
}

export async function getChatMessagesRequest(
    chatId: string,
): Promise<ChatMessage[]> {
    await wait();
    return MOCK_MESSAGES_BY_CHAT_ID[chatId] ?? [];
}

function updateChatPreview(chatId: string, lastMessage: string) {
    const timeLabel = getCurrentTimeLabel();
    MOCK_CHATS = MOCK_CHATS.map((chat) =>
        chat.id === chatId
            ? { ...chat, lastMessage, updatedAt: timeLabel }
            : chat,
    );
}

export async function startChatWithFriendRequest(
    friend: SuggestedFriend,
): Promise<ChatPreview> {
    await wait();

    const existing = MOCK_CHATS.find(
        (chat) => chat.suggestedFriendId === friend.id,
    );
    if (existing) {
        return existing;
    }

    const chatId = `friend-${friend.id}`;
    const newChat: ChatPreview = {
        id: chatId,
        name: friend.name,
        lastMessage: "Начните переписку",
        updatedAt: getCurrentTimeLabel(),
        suggestedFriendId: friend.id,
    };

    MOCK_CHATS = [newChat, ...MOCK_CHATS];
    MOCK_MESSAGES_BY_CHAT_ID[chatId] = [];

    return newChat;
}

export async function appendChatMessageRequest(
    chatId: string,
    text: string,
): Promise<ChatMessage> {
    await wait();

    const newMessage: ChatMessage = {
        id: `m-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        chatId,
        senderName: "You",
        isMine: true,
        text,
        sentAt: getCurrentTimeLabel(),
    };

    if (!MOCK_MESSAGES_BY_CHAT_ID[chatId]) {
        MOCK_MESSAGES_BY_CHAT_ID[chatId] = [];
    }

    MOCK_MESSAGES_BY_CHAT_ID[chatId].push(newMessage);
    updateChatPreview(chatId, text);

    return newMessage;
}
