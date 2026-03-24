export interface Interest {
    id: string;
    name: string;
    userCount?: number; // Количество пользователей, добавивших этот интерес
}

export interface InterestSearchResult extends Interest {
    userCount: number;
    matchScore?: number; // Оценка совпадения при поиске/вводе своих интересов
}

export interface DefaultInterest extends Interest {
    img?: string;
}
