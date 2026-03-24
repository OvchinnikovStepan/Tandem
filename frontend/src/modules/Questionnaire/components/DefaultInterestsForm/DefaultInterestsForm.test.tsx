import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import DefaultInterestsForm from "./DefaultInterestsForm";
import { type Mock, vi } from "vitest";
import { useSetAtom } from "jotai";

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

vi.mock(
    "@/modules/Questionnaire/components/DefaultInterestsList/DefaultInterestsList.tsx",
    () => ({
        default: () => (
            <div data-testid="default-interests-list">{"Interests list"}</div>
        ),
    }),
);

describe("DefaultInterestsForm", () => {
    beforeEach(() => {
        useSetAtomMock.mockReset();
    });

    it("рендерит список интересов и кнопку 'Далее'", () => {
        useSetAtomMock.mockReturnValue(vi.fn());

        render(<DefaultInterestsForm />);

        expect(
            screen.getByTestId("default-interests-list"),
        ).toBeInTheDocument();

        expect(
            screen.getByRole("button", { name: "Далее" }),
        ).toBeInTheDocument();
    });

    it("переходит к следующему шагу при клике по кнопке 'Далее'", async () => {
        const user = userEvent.setup();
        const setSelectedForm = vi.fn();

        useSetAtomMock.mockReturnValue(setSelectedForm);

        render(<DefaultInterestsForm />);

        const button = screen.getByRole("button", { name: "Далее" });
        await user.click(button);

        expect(setSelectedForm).toHaveBeenCalledWith(1);
    });
});
