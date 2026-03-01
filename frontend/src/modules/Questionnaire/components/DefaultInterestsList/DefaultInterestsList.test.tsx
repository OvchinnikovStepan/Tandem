import { render, screen } from "@testing-library/react";
import DefaultInterestsList from "./DefaultInterestsList";
import { type Mock, vi } from "vitest";
import { useAtom, useAtomValue } from "jotai";

vi.mock("jotai", async (importOriginal) => {
    const actual = (await importOriginal()) as Mock<
        Mock<typeof importOriginal>
    >;
    return {
        ...actual,
        useAtom: vi.fn(),
        useAtomValue: vi.fn(),
    };
});

const useAtomMock = useAtom as unknown as Mock;
const useAtomValueMock = useAtomValue as unknown as Mock;

vi.mock("@/hooks/useUserInterests.ts", () => ({
    useUserInterests: () => ({
        toggleInterest: vi.fn(),
    }),
}));

vi.mock(
    "@/modules/Questionnaire/components/DefaultInterestCard/DefaultInterestCard.tsx",
    () => ({
        DefaultInterestCard: ({ interestName }: { interestName: string }) => (
            <div data-testid="interest-card">{interestName}</div>
        ),
    }),
);

describe("DefaultInterestsList", () => {
    beforeEach(() => {
        useAtomMock.mockReset();
        useAtomValueMock.mockReset();
    });

    it("показывает состояние загрузки, пока интересы не загружены", () => {
        useAtomMock.mockReturnValue([[], vi.fn()]);
        useAtomValueMock.mockImplementation(() => {
            throw new Promise(() => {});
        });

        render(<DefaultInterestsList />);

        expect(screen.getByText("Загрузка интересов...")).toBeInTheDocument();
    });

    it("рендерит карточки интересов после загрузки", () => {
        useAtomMock.mockReturnValue([[], vi.fn()]);
        useAtomValueMock.mockReturnValue([
            { id: "basketball", name: "Баскетбол" },
            { id: "music", name: "Музыка" },
        ]);

        render(<DefaultInterestsList />);

        const cards = screen.getAllByTestId("interest-card");
        expect(cards).toHaveLength(2);
    });
});
