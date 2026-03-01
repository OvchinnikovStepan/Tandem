import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import HeroBlock from "./HeroBlock";

describe("HeroBlock", () => {
    const renderHero = () =>
        render(
            <MemoryRouter>
                <HeroBlock />
            </MemoryRouter>,
        );

    it("отображает заголовок, описание и CTA-кнопку", () => {
        renderHero();

        expect(
            screen.getByRole("heading", {
                level: 1,
                name: "Tandem: ваш корпоративный мессенджер для интересного общения",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByText(/Работа — это не только задачи\./),
        ).toBeInTheDocument();
        expect(
            screen.getByText(/Это люди, с которыми хочется общаться/),
        ).toBeInTheDocument();

        expect(
            screen.getByRole("link", { name: "Попробовать бесплатно" }),
        ).toBeInTheDocument();
    });

    it("отображает hero-изображение с корректным alt", () => {
        renderHero();

        const image = screen.getByRole("img", { name: "Hero Image" });
        expect(image).toBeInTheDocument();
    });
});
