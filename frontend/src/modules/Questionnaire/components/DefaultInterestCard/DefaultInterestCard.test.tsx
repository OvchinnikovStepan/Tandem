import { render, screen } from "@testing-library/react";
import { DefaultInterestCard } from "./DefaultInterestCard";

describe("DefaultInterestCard", () => {
    it("отображает название интереса и иконку", () => {
        render(
            <DefaultInterestCard
                interestName="Баскетбол"
                iconPath="/interestsIcons/basketball.svg"
                gradient="bg-red-500"
            />,
        );

        expect(screen.getByText("Баскетбол")).toBeInTheDocument();

        const img = screen.getByRole("img", { name: "Icon" });
        expect(img).toHaveAttribute("src", "/interestsIcons/basketball.svg");
    });

    it("добавляет выделение при isSelected=true", () => {
        const { container } = render(
            <DefaultInterestCard
                interestName="Музыка"
                gradient="bg-blue-500"
                isSelected
            />,
        );

        const button = container.querySelector("button");
        expect(button).toBeInTheDocument();
        expect(button?.className).toContain("shadow-default");
    });
});
