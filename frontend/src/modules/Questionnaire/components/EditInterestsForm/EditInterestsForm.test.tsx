import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import EditInterestsForm from "./EditInterestsForm";
import { type Mock, vi } from "vitest";
import { useAtom } from "jotai";

vi.mock("jotai", async (importOriginal) => {
    const actual = (await importOriginal()) as Mock<
        Mock<typeof importOriginal>
    >;
    return {
        ...actual,
        useAtom: vi.fn(),
    };
});

const useAtomMock = useAtom as unknown as Mock;

vi.mock("@/hooks/useUserInterests", () => ({
    useUserInterests: () => ({
        toggleInterest: vi.fn(),
        createInterest: vi
            .fn()
            .mockResolvedValue({ id: "custom", name: "Custom" }),
    }),
}));

vi.mock("@/hooks/useInterestSearch", () => ({
    useInterestSearch: () => ({
        results: [],
        isLoading: false,
    }),
}));

vi.mock("@/components/EditInterestSearch/EditInterestSearch.tsx", () => ({
    EditInterestSearch: () => <div data-testid="edit-interest-search" />,
}));

vi.mock("@/components/EditInterestCard/EditInterestCard.tsx", () => ({
    EditInterestCard: () => <div data-testid="edit-interest-card" />,
}));

vi.mock("@/components/InterestSearchResults/InterestSearchResults.tsx", () => ({
    InterestSearchResults: () => <div data-testid="interest-search-results" />,
}));

describe("EditInterestsForm", () => {
    beforeEach(() => {
        useAtomMock.mockReset();
        window.alert = vi.fn();
    });

    it("отображает сообщение, если интересы не выбраны", () => {
        useAtomMock
            // questionnaireStepperAtom
            .mockReturnValueOnce([1, vi.fn()])
            // selectedInterestsAtom
            .mockReturnValueOnce([[], vi.fn()]);

        render(<EditInterestsForm />);

        expect(
            screen.getByText("Необходимо выбрать хотя бы 1 интерес!"),
        ).toBeInTheDocument();
    });

    it("показывает выбранные интересы и активную кнопку 'Далее'", () => {
        const setSelectedForm = vi.fn();

        useAtomMock
            // questionnaireStepperAtom
            .mockReturnValueOnce([1, setSelectedForm])
            // selectedInterestsAtom
            .mockReturnValueOnce([[{ id: "music", name: "Музыка" }], vi.fn()]);

        render(<EditInterestsForm />);

        expect(screen.getByTestId("edit-interest-card")).toBeInTheDocument();

        const nextButton = screen.getByRole("button", { name: "Далее" });
        expect(nextButton).not.toBeDisabled();
    });

    it("переходит к следующему шагу при клике по кнопке 'Далее'", async () => {
        const user = userEvent.setup();
        const setSelectedForm = vi.fn();

        useAtomMock
            // questionnaireStepperAtom
            .mockReturnValueOnce([1, setSelectedForm])
            // selectedInterestsAtom
            .mockReturnValueOnce([[{ id: "music", name: "Музыка" }], vi.fn()]);

        render(<EditInterestsForm />);

        const nextButton = screen.getByRole("button", { name: "Далее" });
        await user.click(nextButton);

        expect(setSelectedForm).toHaveBeenCalledWith(2);
    });
});
