import { render, screen } from "@testing-library/react";
import Questionnaire from "./Questionnaire";
import { type Mock, vi } from "vitest";
import { useAtom } from "jotai";

vi.mock("jotai", () => ({
    useAtom: vi.fn(),
}));

const useAtomMock = useAtom as unknown as Mock;

vi.mock("@/modules/Questionnaire", () => ({
    DefaultInterestsForm: () => (
        <div data-testid="default-interests-form">Default interests</div>
    ),
    EditInterestsForm: () => (
        <div data-testid="edit-interests-form">Edit interests</div>
    ),
    ProfileForm: () => <div data-testid="profile-form">Profile</div>,
    questionnaireStepperAtom: {},
}));

describe("Questionnaire page", () => {
    beforeEach(() => {
        useAtomMock.mockReset();
    });

    it("по умолчанию рендерит форму выбора интересов", () => {
        useAtomMock.mockReturnValue([0, vi.fn()]);

        render(<Questionnaire />);

        expect(
            screen.getByTestId("default-interests-form"),
        ).toBeInTheDocument();
    });

    it("рендерит форму редактирования интересов, когда выбран соответствующий шаг", () => {
        useAtomMock.mockReturnValue([1, vi.fn()]);

        render(<Questionnaire />);

        expect(screen.getByTestId("edit-interests-form")).toBeInTheDocument();
    });

    it("рендерит форму профиля, когда выбран последний шаг", () => {
        useAtomMock.mockReturnValue([2, vi.fn()]);

        render(<Questionnaire />);

        expect(screen.getByTestId("profile-form")).toBeInTheDocument();
    });
});
