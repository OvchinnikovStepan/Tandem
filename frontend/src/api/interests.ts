import type { Interest, InterestSearchResult } from "@/types/interests";

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
    // Например: GET /api/interests/search?q={query}&limit={limit}

    if (!query.trim()) {
        return [];
    }

    // Имитация задержки API
    await new Promise((resolve) => setTimeout(resolve, 500));

    // Моковые данные для демонстрации
    // В будущем это будет запрос к бэкенду:
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
 * Сохранить интересы пользователя при редактировании профиля
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

    const response = await fetch(`api/users/${_userId}/interests`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ interests }),
    });

    if (!response.ok) {
        // throw new Error("Failed to save interests");

        // Пока бэкенд не готов, просто возвращаем успешный промис
        await new Promise((resolve) => setTimeout(resolve, 500));
        return Promise.resolve();
    }
}

/**
 * Создать новый пользовательский интерес
 * @param interestName - название интереса
 * @returns созданный интерес
 * @TODO: будет реализовано в будущем с завершением сервиса интересов
 **/
export async function createCustomInterest(
    interestName: string,
): Promise<Interest> {
    // Пример запроса: POST /api/interests/custom
    // Body: { name: string }

    // Имитация задержки API
    await new Promise((resolve) => setTimeout(resolve, 300));

    // В будущем:
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
