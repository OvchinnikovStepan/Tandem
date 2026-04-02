import { render, screen } from "@testing-library/react";
import FeaturesBlock from "./FeaturesBlock";

describe("FeaturesBlock", () => {
    it("отображает все преимущества из конфигурации", () => {
        render(<FeaturesBlock />);

        const titles = [
            "Безопасная связь",
            "Группы по интересам",
            "Интерактивная анкета",
            "Ничего лишнего",
        ];

        titles.forEach((title) => {
            expect(
                screen.getByRole("heading", { level: 3, name: title }),
            ).toBeInTheDocument();
        });
    });
});
