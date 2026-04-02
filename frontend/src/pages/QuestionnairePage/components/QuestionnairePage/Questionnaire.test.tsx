import { render, screen } from "@testing-library/react";
import Questionnaire from "./Questionnaire";
import { type Mock, vi } from "vitest";
import { useQuestionnaireStepper } from "@/modules/Questionnaire";

vi.mock("@/modules/Questionnaire", () => ({
    DefaultInterestsForm: () => (
        <div data-testid="default-interests-form">{"Default interests"}</div>
    ),
    EditInterestsForm: () => (
        <div data-testid="edit-interests-form">{"Edit interests"}</div>
    ),
    ProfileForm: () => <div data-testid="profile-form">{"Profile"}</div>,
    useQuestionnaireStepper: vi.fn(),
}));

describe("Questionnaire page", () => {
    beforeEach(() => {
        (useQuestionnaireStepper as unknown as Mock).mockReset();
    });

    it("по умолчанию рендерит форму выбора интересов", () => {
        (useQuestionnaireStepper as unknown as Mock).mockReturnValue({
            selectedForm: 0,
        });

        render(<Questionnaire />);

        expect(
            screen.getByTestId("default-interests-form"),
        ).toBeInTheDocument();
    });

    it("рендерит форму редактирования интересов, когда выбран соответствующий шаг", () => {
        (useQuestionnaireStepper as unknown as Mock).mockReturnValue({
            selectedForm: 1,
        });

        render(<Questionnaire />);

        expect(screen.getByTestId("edit-interests-form")).toBeInTheDocument();
    });

    it("рендерит форму профиля, когда выбран последний шаг", () => {
        (useQuestionnaireStepper as unknown as Mock).mockReturnValue({
            selectedForm: 2,
        });

        render(<Questionnaire />);

        expect(screen.getByTestId("profile-form")).toBeInTheDocument();
    });
});
