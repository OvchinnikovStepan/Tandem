import "@testing-library/jest-dom";
import "@/i18n/i18n";
import i18n from "@/i18n/i18n";

beforeEach(async () => {
	localStorage.setItem("language", "ru");
	await i18n.changeLanguage("ru");
});
