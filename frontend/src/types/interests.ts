export interface Interest {
    id: string;
    name: string;
    userCount: number; // Количество пользователей, добавивших этот интерес
}

// Оценка совпадения при поиске/вводе своих интересов (в будущем будет получаться из бэка)
export interface InterestSearchResult extends Interest {
    matchScore?: number;
}

export interface UserInterests {
    userId: string;
    interests: Interest[];
}
