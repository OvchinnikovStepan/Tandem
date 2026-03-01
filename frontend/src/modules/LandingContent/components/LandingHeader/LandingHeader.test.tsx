import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import LandingHeader from "./LandingHeader";
import { describe } from "vitest";

describe("LandingHeader", () => {
    const renderHeader = () =>
        render(
            <MemoryRouter>
                <LandingHeader />
            </MemoryRouter>,
        );

    it("рендерит логотип и кнопку входа", () => {
        renderHeader();

        expect(
            screen.getByRole("img", { name: "Tandem Logo" }),
        ).toBeInTheDocument();

        const loginButton = screen.getByRole("link", { name: "Войти" });
        expect(loginButton).toBeInTheDocument();
    });

    it("рендерит переключатель языка с текущим языком по умолчанию", () => {
        renderHeader();

        expect(
            screen.getByRole("button", { name: /change language/i }),
        ).toBeInTheDocument();

        expect(screen.getByText("Русский")).toBeInTheDocument();
    });
});
