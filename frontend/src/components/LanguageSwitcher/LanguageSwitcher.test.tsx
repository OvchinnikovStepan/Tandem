import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {LanguageSwitcher} from "./LanguageSwitcher";
import i18n from "@/i18n/i18n.ts";

describe("LanguageSwitcher", () => {
    it("по умолчанию отображает текущий язык (ru)", () => {
        i18n.changeLanguage("ru");
        render(<LanguageSwitcher/>);

        expect(
            screen.getByRole("button", {name: /change language/i}),
        ).toBeInTheDocument();
        expect(screen.getByText("Русский")).toBeInTheDocument();
    });

    it("открывает список языков по клику", async () => {
        const user = userEvent.setup();
        render(<LanguageSwitcher/>);

        const toggleButton = screen.getByRole("button", {name: /change language/i});
        await user.click(toggleButton);

        expect(screen.getByRole("button", {name: "Русский"})).toBeInTheDocument();
        expect(screen.getByRole("button", {name: "English"})).toBeInTheDocument();
    });

    it("меняет язык и сохраняет его в localStorage", async () => {
        const user = userEvent.setup();
        render(<LanguageSwitcher/>);

        const toggleButton = screen.getByRole("button", {name: /change language/i});
        await user.click(toggleButton);

        const englishOption = screen.getByRole("button", {name: "English"});
        await user.click(englishOption);

        expect(screen.getByText("English")).toBeInTheDocument();
        expect(localStorage.getItem("language")).toBe("en");
    });

    it("closes dropdown when clicking outside", async () => {
        const user = userEvent.setup();
        render(<LanguageSwitcher />);

        const button = screen.getByLabelText("Change language");
        await user.click(button);

        expect(screen.getByText("English")).toBeInTheDocument();

        // Клик вне dropdown
        await user.click(document.body);

        expect(screen.queryByText("English")).not.toBeInTheDocument();
    });
});
