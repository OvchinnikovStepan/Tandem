import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import DefaultInterestsForm from "./DefaultInterestsForm";
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

vi.mock(
    "@/modules/Questionnaire/components/DefaultInterestsList/DefaultInterestsList.tsx",
    () => ({
        default: () => (
            <div data-testid="default-interests-list">Interests list</div>
        ),
    }),
);

describe("DefaultInterestsForm", () => {
    beforeEach(() => {
        useAtomMock.mockReset();
    });

    it("рендерит список интересов и кнопку 'Далее'", () => {
        // selectedInterestsAtom
        useAtomMock
            .mockReturnValueOnce([
                [{ id: "basketball", name: "Баскетбол" }],
                vi.fn(),
            ])
            // questionnaireStepperAtom
            .mockReturnValueOnce([0, vi.fn()]);

        render(<DefaultInterestsForm />);

        expect(
            screen.getByTestId("default-interests-list"),
        ).toBeInTheDocument();

        expect(
            screen.getByRole("button", { name: "Далее" }),
        ).toBeInTheDocument();
    });

    it("делает кнопку неактивной, когда интересы не выбраны", () => {
        useAtomMock
            // selectedInterestsAtom пустой
            .mockReturnValueOnce([[], vi.fn()])
            // questionnaireStepperAtom
            .mockReturnValueOnce([0, vi.fn()]);

        render(<DefaultInterestsForm />);

        const button = screen.getByRole("button", { name: "Далее" });
        expect(button).toBeDisabled();
    });

    it("переходит к следующему шагу при клике по кнопке 'Далее'", async () => {
        const user = userEvent.setup();
        const setSelectedForm = vi.fn();

        useAtomMock
            // selectedInterestsAtom с выбранным интересом
            .mockReturnValueOnce([[{ id: "music", name: "Музыка" }], vi.fn()])
            // questionnaireStepperAtom
            .mockReturnValueOnce([0, setSelectedForm]);

        render(<DefaultInterestsForm />);

        const button = screen.getByRole("button", { name: "Далее" });
        await user.click(button);

        expect(setSelectedForm).toHaveBeenCalledWith(1);
    });
});
