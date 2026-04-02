import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import Landing from "./Landing";

describe("Landing page", () => {
    it("рендерит все основные блоки лэндинга", () => {
        render(
            <MemoryRouter>
                <Landing />
            </MemoryRouter>,
        );

        expect(screen.getByRole("banner")).toBeInTheDocument();
        expect(
            screen.getByRole("heading", {
                level: 1,
                name: "Tandem: ваш корпоративный мессенджер для интересного общения",
            }),
        ).toBeInTheDocument();
        expect(
            screen.getByRole("heading", {
                level: 2,
                name: "Готовы начать с Tandem?",
            }),
        ).toBeInTheDocument();
        expect(screen.getByRole("contentinfo")).toBeInTheDocument();
    });

    it("имеет все основные call-to-action ссылки и кнопки", () => {
        render(
            <MemoryRouter>
                <Landing />
            </MemoryRouter>,
        );

        const loginLinks = screen.getAllByRole("link", { name: "Войти" });
        expect(loginLinks.length).toBeGreaterThanOrEqual(1);

        const ctaButtons = screen.getAllByRole("link", {
            name: "Попробовать бесплатно",
        });
        expect(ctaButtons.length).toBeGreaterThanOrEqual(2);
    });
});
