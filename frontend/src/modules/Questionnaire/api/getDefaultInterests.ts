import type { Interest } from "@/types/interests";

// Получить список стандартных интересов
export async function getDefaultInterests(): Promise<Interest[]> {
    // TODO: Заменить на реальный запрос к бэкенду
    console.log("Fetching default interests...");

    return new Promise((resolve) => {
        setTimeout(() => {
            resolve([
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
            ]);
        }, 500);
    });
}
