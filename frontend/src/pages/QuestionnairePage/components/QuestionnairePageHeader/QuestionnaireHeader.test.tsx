import { render, screen } from "@testing-library/react";
import QuestionnaireHeader from "./QuestionnaireHeader";
import { vi, type Mock } from "vitest";
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

describe("QuestionnaireHeader", () => {
    beforeEach(() => {
        useAtomMock.mockReset();
    });

    it("рендерит логотип и заголовок анкеты", () => {
        useAtomMock.mockReturnValue([0, vi.fn()]);

        render(<QuestionnaireHeader />);

        expect(screen.getByRole("img", { name: "Tandem" })).toBeInTheDocument();

        expect(
            screen.getByRole("heading", { name: "Анкета" }),
        ).toBeInTheDocument();
    });

    it("отображает описание для шага по умолчанию", () => {
        useAtomMock.mockReturnValue([0, vi.fn()]);

        render(<QuestionnaireHeader />);

        expect(
            screen.getByText(/расскажите нам о ваших интересах/i),
        ).toBeInTheDocument();
    });

    it("отображает описание для шага редактирования интересов", () => {
        useAtomMock.mockReturnValue([1, vi.fn()]);

        render(<QuestionnaireHeader />);

        expect(
            screen.getByText("Пожалуйста, напишите ниже другие свои интересы!"),
        ).toBeInTheDocument();
    });

    it("отображает описание для шага профиля", () => {
        useAtomMock.mockReturnValue([2, vi.fn()]);

        render(<QuestionnaireHeader />);

        expect(
            screen.getByText("Пожалуйста, напишите ниже информацию о себе!"),
        ).toBeInTheDocument();
    });
});
