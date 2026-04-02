import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import QuestionBlock from "./QuestionBlock";

describe("QuestionBlock", () => {
    const renderBlock = () =>
        render(
            <MemoryRouter>
                <QuestionBlock />
            </MemoryRouter>,
        );

    it("отображает заголовок, описание и CTA-кнопку", () => {
        renderBlock();

        expect(
            screen.getByRole("heading", {
                level: 2,
                name: "Готовы начать с Tandem?",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByText(
                "Заполните анкету, а Tandem подберёт людей и группы, подходящие именно вам и вашим интересам!",
            ),
        ).toBeInTheDocument();

        expect(
            screen.getByRole("link", { name: "Попробовать бесплатно" }),
        ).toBeInTheDocument();
    });
});
