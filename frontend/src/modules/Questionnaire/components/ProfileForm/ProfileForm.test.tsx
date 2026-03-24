import { render, screen, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfileForm from "./ProfileForm";
import { MemoryRouter } from "react-router";
import { type Mock, vi } from "vitest";
import { useSetAtom } from "jotai";
import { useSubmitQuestionnaire } from "@/modules/Questionnaire/hooks/useSubmitQuestionnaire";

const useSetAtomMock = useSetAtom as unknown as Mock;
const saveQuestionnaireMock = vi.fn();

vi.mock("jotai", async (importOriginal) => {
    const actual = (await importOriginal()) as Mock<
        Mock<typeof importOriginal>
    >;
    return {
        ...actual,
        useSetAtom: vi.fn(),
    };
});

vi.mock("@/modules/Questionnaire/hooks/useSubmitQuestionnaire", () => ({
    useSubmitQuestionnaire: vi.fn(),
}));

describe("ProfileForm", () => {
    beforeEach(() => {
        useSetAtomMock.mockReset();
        saveQuestionnaireMock.mockReset();
        (useSubmitQuestionnaire as unknown as Mock).mockReset();
        (useSubmitQuestionnaire as unknown as Mock).mockReturnValue({
            saveQuestionnaire: saveQuestionnaireMock,
            isPending: false,
            error: null,
        });
    });

    it("отображает ошибки валидации, если обязательные поля не заполнены", async () => {
        useSetAtomMock.mockReturnValue(vi.fn());

        render(
            <MemoryRouter>
                <ProfileForm />
            </MemoryRouter>,
        );

        const form = document.getElementById("profile-form") as HTMLFormElement;
        fireEvent.submit(form);

        expect(
            await screen.findByText("Пожалуйста, напишите свое имя."),
        ).toBeInTheDocument();
        expect(
            await screen.findByText("Пожалуйста, напишите свою фамилию."),
        ).toBeInTheDocument();
    });

    it("успешно сохраняет профиль и переходит на главную страницу", async () => {
        const user = userEvent.setup();

        useSetAtomMock.mockReturnValue(vi.fn());

        render(
            <MemoryRouter>
                <ProfileForm />
            </MemoryRouter>,
        );

        await user.type(screen.getByLabelText(/Имя/i), "Иван");
        await user.type(screen.getByLabelText(/Фамилия/i), "Иванов");

        const submitButton = screen.getByRole("button", { name: "Готово" });
        await user.click(submitButton);

        expect(saveQuestionnaireMock).toHaveBeenCalledWith(
            expect.objectContaining({
                firstName: "Иван",
                lastName: "Иванов",
            }),
        );
    });

    it("возвращает на предыдущий шаг по кнопке 'Назад'", async () => {
        const user = userEvent.setup();
        const setSelectedForm = vi.fn();
        useSetAtomMock.mockReturnValue(setSelectedForm);

        render(
            <MemoryRouter>
                <ProfileForm />
            </MemoryRouter>,
        );

        await user.click(screen.getByRole("button", { name: "Назад" }));

        expect(setSelectedForm).toHaveBeenCalledWith(1);
    });
});
