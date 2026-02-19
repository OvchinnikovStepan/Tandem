import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import ruTranslations from "./locales/ru.json";
import enTranslations from "./locales/en.json";

const savedLanguage = localStorage.getItem("language");
const language = savedLanguage === "ru" || savedLanguage === "en" ? savedLanguage : "ru";

if (!i18n.isInitialized) {
  void i18n.use(initReactI18next).init({
    resources: {
      ru: {
        translation: ruTranslations,
      },
      en: {
        translation: enTranslations,
      },
    },
    lng: language,
    fallbackLng: "ru",
  });
}

export default i18n;