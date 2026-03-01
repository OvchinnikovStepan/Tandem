// API функции для работы с интересами
// Эти функции будут заменены на реальные запросы к бэкенду

import type { Interest, InterestSearchResult } from "@/types/interests";

// Переменная будет использована при интеграции с реальным API
// В реальном приложении используйте: const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

/**
 * Поиск интересов по запросу
 * @param query - поисковый запрос
 * @param limit - максимальное количество результатов
 * @returns список подходящих интересов с количеством пользователей
 */
export async function searchInterests(
    query: string,
    limit: number = 10,
): Promise<InterestSearchResult[]> {
    // TODO: Заменить на реальный запрос к бэкенду
    // Пример запроса: GET /api/interests/search?q={query}&limit={limit}

    if (!query.trim()) {
        return [];
    }

    // Имитация задержки API
    await new Promise((resolve) => setTimeout(resolve, 300));

    // Моковые данные для демонстрации
    // В реальном приложении это будет запрос к бэкенду:
    // const response = await fetch(`${API_BASE_URL}/interests/search?q=${encodeURIComponent(query)}&limit=${limit}`);
    // return await response.json();

    const mockInterests: InterestSearchResult[] = [
        {
            id: "1",
            name: "Разработка на React",
            userCount: 45,
            matchScore: 0.9,
        },
        {
            id: "2",
            name: "Разработка на TypeScript",
            userCount: 38,
            matchScore: 0.85,
        },
        {
            id: "3",
            name: "Разработка мобильных приложений",
            userCount: 22,
            matchScore: 0.7,
        },
        {
            id: "4",
            name: "Разработка игр",
            userCount: 15,
            matchScore: 0.6,
        },
    ];

    // Фильтруем по запросу (потом это будет на бэкенде)
    const queryLower = query.toLowerCase();
    return mockInterests
        .filter((interest) => interest.name.toLowerCase().includes(queryLower))
        .slice(0, limit);
}

/**
 * Используется для получения списока стандартных интересов (но пока что они хранятся на фронте)
 * @returns список стандартных интересов
 */
export async function getDefaultInterests(): Promise<Interest[]> {
    // TODO: Заменить на реальный запрос к бэкенду
    // Пример запроса: GET /api/interests/default

    // Моковые данные согласно дизайну
    return [
        { id: "basketball", name: "Баскетбол", userCount: 120 },
        { id: "football", name: "Футбол", userCount: 95 },
        { id: "volleyball", name: "Воллейбол", userCount: 80 },
        { id: "music", name: "Музыка", userCount: 180 },
        { id: "running", name: "Бег", userCount: 150 },
        { id: "reading", name: "Чтение", userCount: 90 },
        { id: "swimming", name: "Плавание", userCount: 110 },
        { id: "anime", name: "Аниме", userCount: 200 },
        { id: "drawing", name: "Рисование", userCount: 85 },
        { id: "cooking", name: "Кулинария", userCount: 75 },
        { id: "development", name: "Разработка", userCount: 200 },
        { id: "travel", name: "Путешествия", userCount: 110 },
        { id: "photography", name: "Фотография", userCount: 65 },
        { id: "neural-networks", name: "Нейросети", userCount: 140 },
        { id: "games", name: "Игры", userCount: 150 },
        { id: "movies", name: "Фильмы", userCount: 140 },
        { id: "hockey", name: "Хоккей", userCount: 70 },
        { id: "cycling", name: "Велоспорт", userCount: 95 },
        { id: "astronomy", name: "Астрономия", userCount: 50 },
        { id: "cars", name: "Автомобили", userCount: 130 },
        { id: "biology", name: "Биология", userCount: 60 },
        { id: "career", name: "Карьера", userCount: 180 },
        { id: "pets", name: "Питомцы", userCount: 160 },
        { id: "gardening", name: "Садоводство", userCount: 55 },
    ];
}

/**
 * Сохранить интересы пользователя
 * @param _userId - ID пользователя
 * @param interests - массив интересов
 */
export async function saveUserInterests(
    _userId: string,
    interests: Interest[],
): Promise<void> {
    // TODO: Заменить на реальный запрос к бэкенду
    // Пример запроса: POST /api/users/{userId}/interests
    // Body: { interests: string[] }

    // Имитация задержки API
    await new Promise((resolve) => setTimeout(resolve, 500));

    // В реальном приложении:
    // const response = await fetch(`${API_BASE_URL}/users/${_userId}/interests`, {
    //   method: "POST",
    //   headers: {
    //     "Content-Type": "application/json",
    //   },
    //   body: JSON.stringify({ interests }),
    // });
    // if (!response.ok) {
    //   throw new Error("Failed to save interests");
    // }

    console.log("Saving interests for user:", _userId, interests);
}

/**
 * Создать новый пользовательский интерес
 * @param interestName - название интереса
 * @returns созданный интерес
 */
export async function createCustomInterest(
    interestName: string,
): Promise<Interest> {
    // TODO: Заменить на реальный запрос к бэкенду
    // Пример запроса: POST /api/interests/custom
    // Body: { name: string }

    // Имитация задержки API
    await new Promise((resolve) => setTimeout(resolve, 300));

    // В реальном приложении:
    // const response = await fetch(`${API_BASE_URL}/interests/custom`, {
    //   method: "POST",
    //   headers: {
    //     "Content-Type": "application/json",
    //   },
    //   body: JSON.stringify({ name: interestName }),
    // });
    // return await response.json();

    return {
        id: `custom-${interestName.toLowerCase()}`,
        name: interestName,
        userCount: 1,
    };
}
