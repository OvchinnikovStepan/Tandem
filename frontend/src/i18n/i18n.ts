import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import ruTranslations from "./locales/ru.json";
import enTranslations from "./locales/en.json";

i18n
    .use(initReactI18next)
    .init({
        resources: {
            ru: {
                translation: ruTranslations,
            },
            en: {
                translation: enTranslations,
            },
        },
        lng: localStorage.getItem("language") || "ru",
        fallbackLng: "ru",
    });

export default i18n;
