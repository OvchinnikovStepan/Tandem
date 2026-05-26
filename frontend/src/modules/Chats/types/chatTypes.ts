export type ChatPreview = {
    id: string;
    name: string;
    lastMessage: string;
    updatedAt: string;
    unreadCount?: number;
    isMuted?: boolean;
    isPinned?: boolean;
    avatarUrl?: string;
    suggestedFriendId?: string;
};

export type SuggestedFriend = {
    id: string;
    name: string;
    username: string;
};

export type ChatMessage = {
    id: string;
    chatId: string;
    senderName: string;
    isMine: boolean;
    text?: string;
    sentAt: string;
    attachment?: {
        type: "image";
        label: string;
        imageUrl?: string;
    };
};
