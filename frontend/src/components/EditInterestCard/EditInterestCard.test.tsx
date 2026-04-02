import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { EditInterestCard } from "./EditInterestCard";
import type { Interest } from "@/types/interests";

describe("EditInterestCard", () => {
    const interest: Interest = { id: "music", name: "Музыка" };

    it("отображает название интереса", () => {
        render(<EditInterestCard interest={interest} />);

        expect(screen.getByText("Музыка")).toBeInTheDocument();
    });

    it("удаляет интерес по клику на иконку", async () => {
        const user = userEvent.setup();
        const toggleInterest = vi.fn();
        const { container } = render(
            <EditInterestCard
                interest={interest}
                toggleInterest={toggleInterest}
            />,
        );

        const trashIcon = container.querySelector("svg");
        expect(trashIcon).toBeTruthy();

        await user.click(trashIcon!);

        expect(toggleInterest).toHaveBeenCalledWith(interest);
    });
});
