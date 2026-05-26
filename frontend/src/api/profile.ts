// API функции для работы с профилем пользователя

// Переменная будет использована при интеграции с реальным API
// В реальном приложении используйте: const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

export interface ProfileData {
    firstName: string;
    lastName: string;
    city?: string;
    birthDate?: string;
    gender?: string;
    interests: string[];
}

/**
 * Сохранить данные профиля пользователя
 * @param _userId - ID пользователя
 * @param _profileData - данные профиля
 */
export async function saveUserProfile(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    _userId: string,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    _profileData: ProfileData,
): Promise<void> {
    // TODO: Заменить на реальный запрос к бэкенду
    // Пример запроса: POST /api/users/{userId}/profile
    // Body: ProfileData

    // Имитация задержки API
    await new Promise((resolve) => setTimeout(resolve, 500));

    // В реальном приложении:
    // const response = await fetch(`${API_BASE_URL}/users/${userId}/profile`, {
    //   method: "POST",
    //   headers: {
    //     "Content-Type": "application/json",
    //   },
    //   body: JSON.stringify(profileData),
    // });
    // if (!response.ok) {
    //   throw new Error("Failed to save profile");
    // }
}

/**
 * Получить данные профиля пользователя
 * @param _userId - ID пользователя
 * @returns данные профиля
 */
export async function getUserProfile(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    _userId: string,
): Promise<ProfileData | null> {
    // TODO: Заменить на реальный запрос к бэкенду
    // Пример запроса: GET /api/users/{userId}/profile

    // Имитация задержки API
    await new Promise((resolve) => setTimeout(resolve, 300));

    // В реальном приложении:
    // const response = await fetch(`${API_BASE_URL}/users/${userId}/profile`);
    // if (response.status === 404) {
    //   return null;
    // }
    // if (!response.ok) {
    //   throw new Error("Failed to load profile");
    // }
    // return await response.json();

    return null;
}
