import type { DefaultInterest } from "@/types/interests";

// Получить список стандартных интересов
export async function getDefaultInterests(
    loc?: string,
): Promise<DefaultInterest[]> {
    // TODO: Заменить на реальный запрос к бэкенду

    // В реальном приложении:
    // const response = await fetch(`${API_BASE_URL}/interests/tags/default/${loc}`, {
    //   method: "GET"
    // });
    // if (!response.ok) {
    //   throw new Error("Failed to get default interests");
    // }

    const names =
        loc === "ru"
            ? [
                  "Баскетбол",
                  "Футбол",
                  "Волейбол",
                  "Музыка",
                  "Бег",
                  "Чтение",
                  "Плавание",
                  "Аниме",
                  "Рисование",
                  "Кулинария",
                  "Разработка",
                  "Путешествия",
                  "Фотография",
                  "Нейросети",
                  "Игры",
                  "Фильмы",
                  "Хоккей",
                  "Велоспорт",
                  "Астрономия",
                  "Автомобили",
                  "Биология",
                  "Карьера",
                  "Питомцы",
                  "Садоводство",
              ]
            : [
                  "Basketball",
                  "Football",
                  "Volleyball",
                  "Music",
                  "Running",
                  "Reading",
                  "Swimming",
                  "Anime",
                  "Drawing",
                  "Cooking",
                  "Development",
                  "Travel",
                  "Photography",
                  "AI",
                  "Games",
                  "Movies",
                  "Hockey",
                  "Cycling",
                  "Astronomy",
                  "Cars",
                  "Biology",
                  "Career",
                  "Pets",
                  "Gardening",
              ];

    console.log("Fetching...");

    return new Promise((resolve) => {
        setTimeout(() => {
            resolve([
                {
                    id: `basketball-${loc}`,
                    name: names[0],
                    img: "interestsIcons/basketball.svg",
                },
                {
                    id: `football-${loc}`,
                    name: names[1],
                    img: "interestsIcons/football.svg",
                },
                {
                    id: `volleyball-${loc}`,
                    name: names[2],
                    img: "interestsIcons/volleyball.svg",
                },
                {
                    id: `music-${loc}`,
                    name: names[3],
                    img: "interestsIcons/music.svg",
                },
                {
                    id: `running-${loc}`,
                    name: names[4],
                    img: "interestsIcons/running.svg",
                },
                {
                    id: `reading-${loc}`,
                    name: names[5],
                    img: "interestsIcons/reading.svg",
                },
                {
                    id: `swimming-${loc}`,
                    name: names[6],
                    img: "interestsIcons/swimming.svg",
                },
                {
                    id: `anime-${loc}`,
                    name: names[7],
                    img: "interestsIcons/anime.svg",
                },
                {
                    id: `drawing-${loc}`,
                    name: names[8],
                    img: "interestsIcons/drawing.svg",
                },
                {
                    id: `cooking-${loc}`,
                    name: names[9],
                    img: "interestsIcons/cooking.svg",
                },
                {
                    id: `development-${loc}`,
                    name: names[10],
                    img: "interestsIcons/development.svg",
                },
                {
                    id: `travel-${loc}`,
                    name: names[11],
                    img: "interestsIcons/travel.svg",
                },
                {
                    id: `photography-${loc}`,
                    name: names[12],
                    img: "interestsIcons/photography.svg",
                },
                {
                    id: `neural-networks-${loc}`,
                    name: names[13],
                    img: "interestsIcons/neural-networks.svg",
                },
                {
                    id: `games-${loc}`,
                    name: names[14],
                    img: "interestsIcons/games.svg",
                },
                {
                    id: `movies-${loc}`,
                    name: names[15],
                    img: "interestsIcons/movies.svg",
                },
                {
                    id: `hockey-${loc}`,
                    name: names[16],
                    img: "interestsIcons/hockey.svg",
                },
                {
                    id: `cycling-${loc}`,
                    name: names[17],
                    img: "interestsIcons/cycling.svg",
                },
                {
                    id: `astronomy-${loc}`,
                    name: names[18],
                    img: "interestsIcons/astronomy.svg",
                },
                {
                    id: `cars-${loc}`,
                    name: names[19],
                    img: "interestsIcons/cars.svg",
                },
                {
                    id: `biology-${loc}`,
                    name: names[20],
                    img: "interestsIcons/biology.svg",
                },
                {
                    id: `career-${loc}`,
                    name: names[21],
                    img: "interestsIcons/career.svg",
                },
                {
                    id: `pets-${loc}`,
                    name: names[22],
                    img: "interestsIcons/pets.svg",
                },
                {
                    id: `gardening-${loc}`,
                    name: names[23],
                    img: "interestsIcons/gardening.svg",
                },
            ]);
        }, 890);
    });
}
