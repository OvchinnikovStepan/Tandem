import { render, screen, within } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import Footer from "./Footer";

describe("Footer", () => {
    const renderFooter = () =>
        render(
            <MemoryRouter>
                <Footer />
            </MemoryRouter>,
        );

    it("отображает логотип и контактный email", () => {
        renderFooter();

        expect(
            screen.getByRole("img", { name: "Tandem Logo" }),
        ).toBeInTheDocument();

        const contactText = screen.getByText(/По всем вопросам обращайтесь на/);
        expect(contactText).toBeInTheDocument();

        const emailLink = screen.getByRole("link", {
            name: "tandem@t-bang.ru",
        });
        expect(emailLink).toBeInTheDocument();
    });

    it("отображает все навигационные ссылки футера", () => {
        renderFooter();

        const list = screen.getByRole("list");
        const { getAllByRole } = within(list);
        const items = getAllByRole("listitem");
        expect(items.length).toBe(6);

        const expectedLinks = [
            "Почта",
            "ВКонтакте",
            "Телеграм",
            "Оферта",
            "Сведения об организации",
            "Политика обработки персональных данных",
        ];

        expectedLinks.forEach((text) => {
            expect(
                screen.getByRole("link", { name: text }),
            ).toBeInTheDocument();
        });
    });
});
