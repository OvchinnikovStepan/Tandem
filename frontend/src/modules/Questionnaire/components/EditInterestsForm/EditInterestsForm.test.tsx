import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import EditInterestsForm from "./EditInterestsForm";
import { type Mock, vi } from "vitest";
import { useSetAtom } from "jotai";
import { useToggleInterest } from "@/hooks/useToggleInterest";

vi.mock("jotai", async (importOriginal) => {
    const actual = (await importOriginal()) as Mock<
        Mock<typeof importOriginal>
    >;
    return {
        ...actual,
        useSetAtom: vi.fn(),
    };
});

const useSetAtomMock = useSetAtom as unknown as Mock;

vi.mock("@/hooks/useToggleInterest.ts", () => ({
    useToggleInterest: vi.fn(),
}));

vi.mock("@/components/InterestSearch", () => ({
    InterestSearch: () => <div data-testid="interest-search" />,
}));

vi.mock("@/components/EditInterestCard/EditInterestCard.tsx", () => ({
    EditInterestCard: () => <div data-testid="edit-interest-card" />,
}));

describe("EditInterestsForm", () => {
    beforeEach(() => {
        useSetAtomMock.mockReset();
        (useToggleInterest as unknown as Mock).mockReset();
    });

    it("отображает сообщение, если интересы не выбраны", () => {
        useSetAtomMock.mockReturnValue(vi.fn());
        (useToggleInterest as unknown as Mock).mockReturnValue({
            selectedInterests: [],
            toggleInterest: vi.fn(),
        });

        render(<EditInterestsForm />);

        expect(
            screen.getByText("Необходимо выбрать хотя бы 1 интерес!"),
        ).toBeInTheDocument();
    });

    it("показывает выбранные интересы и активную кнопку 'Далее'", () => {
        useSetAtomMock.mockReturnValue(vi.fn());
        (useToggleInterest as unknown as Mock).mockReturnValue({
            selectedInterests: [{ id: "music", name: "Музыка" }],
            toggleInterest: vi.fn(),
        });

        render(<EditInterestsForm />);

        expect(screen.getByTestId("edit-interest-card")).toBeInTheDocument();

        const nextButton = screen.getByRole("button", { name: "Далее" });
        expect(nextButton).not.toBeDisabled();
    });

    it("переходит к следующему шагу при клике по кнопке 'Далее'", async () => {
        const user = userEvent.setup();
        const setSelectedForm = vi.fn();

        useSetAtomMock.mockReturnValue(setSelectedForm);
        (useToggleInterest as unknown as Mock).mockReturnValue({
            selectedInterests: [{ id: "music", name: "Музыка" }],
            toggleInterest: vi.fn(),
        });

        render(<EditInterestsForm />);

        const nextButton = screen.getByRole("button", { name: "Далее" });
        await user.click(nextButton);

        expect(setSelectedForm).toHaveBeenCalledWith(2);
    });

    it("переходит назад при клике на кнопку 'Назад'", async () => {
        const user = userEvent.setup();
        const setSelectedForm = vi.fn();

        useSetAtomMock.mockReturnValue(setSelectedForm);
        (useToggleInterest as unknown as Mock).mockReturnValue({
            selectedInterests: [{ id: "music", name: "Музыка" }],
            toggleInterest: vi.fn(),
        });

        render(<EditInterestsForm />);

        await user.click(screen.getByRole("button", { name: "Назад" }));

        expect(setSelectedForm).toHaveBeenCalledWith(0);
    });
});
