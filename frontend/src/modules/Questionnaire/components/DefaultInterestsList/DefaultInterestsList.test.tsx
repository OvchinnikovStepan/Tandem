import { render, screen } from "@testing-library/react";
import DefaultInterestsList from "./DefaultInterestsList";
import { type Mock, vi } from "vitest";
import { useToggleInterest } from "@/hooks/useToggleInterest";
import { useDefaultInterests } from "@/modules/Questionnaire/hooks/useDefaultInterests";

vi.mock("@/hooks/useToggleInterest.ts", () => ({
    useToggleInterest: vi.fn(),
}));

vi.mock("@/modules/Questionnaire/hooks/useDefaultInterests.ts", () => ({
    useDefaultInterests: vi.fn(),
}));

vi.mock(
    "@/modules/Questionnaire/components/DefaultInterestCard/DefaultInterestCard.tsx",
    () => ({
        DefaultInterestCard: ({
            interestName,
            isSelected,
        }: {
            interestName: string;
            isSelected: boolean;
        }) => (
            <div data-testid="interest-card">
                {`${interestName}-${isSelected ? "active" : "inactive"}`}
            </div>
        ),
    }),
);

describe("DefaultInterestsList", () => {
    beforeEach(() => {
        (useToggleInterest as unknown as Mock).mockReset();
        (useDefaultInterests as unknown as Mock).mockReset();
    });

    it("рендерит карточки интересов после загрузки", () => {
        (useDefaultInterests as unknown as Mock).mockReturnValue({
            defaultInterests: [
                { id: "basketball", name: "Баскетбол", img: "/basketball.svg" },
                { id: "music", name: "Музыка", img: "/music.svg" },
            ],
        });
        (useToggleInterest as unknown as Mock).mockReturnValue({
            toggleInterest: vi.fn(),
            isSelected: vi.fn().mockReturnValue(false),
        });

        render(<DefaultInterestsList />);

        const cards = screen.getAllByTestId("interest-card");
        expect(cards).toHaveLength(2);
    });

    it("передаёт в карточку признак выбранного интереса", () => {
        (useDefaultInterests as unknown as Mock).mockReturnValue({
            defaultInterests: [
                { id: "music", name: "Музыка", img: "/music.svg" },
            ],
        });
        (useToggleInterest as unknown as Mock).mockReturnValue({
            toggleInterest: vi.fn(),
            isSelected: vi.fn().mockReturnValue(true),
        });

        render(<DefaultInterestsList />);

        expect(screen.getByTestId("interest-card")).toHaveTextContent("active");
    });
});
