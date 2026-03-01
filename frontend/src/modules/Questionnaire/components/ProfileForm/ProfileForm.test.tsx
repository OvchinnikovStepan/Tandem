import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfileForm from "./ProfileForm";
import { MemoryRouter } from "react-router";
import { type Mock, vi } from "vitest";
import { useAtom } from "jotai";

const useAtomMock = useAtom as unknown as Mock;
const saveUserProfileMock = vi.fn().mockResolvedValue(undefined);
const navigateMock = vi.fn();

vi.mock("jotai", async (importOriginal) => {
    const actual = (await importOriginal()) as Mock<
        Mock<typeof importOriginal>
    >;
    return {
        ...actual,
        useAtom: vi.fn(),
    };
});

vi.mock("react-router", async (importOriginal) => {
    const actual = await importOriginal<typeof import("react-router")>();
    return {
        ...actual,
        useNavigate: () => navigateMock,
    };
});

vi.mock("@/api/profile", () => ({
    saveUserProfile: (...args: unknown[]) => saveUserProfileMock(...args),
}));

describe("ProfileForm", () => {
    beforeEach(() => {
        useAtomMock.mockReset();
        saveUserProfileMock.mockReset();
        navigateMock.mockReset();
        localStorage.clear();
    });

    it("отображает ошибки валидации, если обязательные поля не заполнены", async () => {
        userEvent.setup();

        useAtomMock.mockReturnValue([2, vi.fn()]);

        render(
            <MemoryRouter>
                <ProfileForm />
            </MemoryRouter>,
        );

        const form = document.getElementById("profile-form") as HTMLFormElement;
        fireEvent.submit(form);

        expect(
            await screen.findByText("Пожалуйста, заполните обязательные поля"),
        ).toBeInTheDocument();
    });

    it("успешно сохраняет профиль и переходит на главную страницу", async () => {
        const user = userEvent.setup();

        useAtomMock.mockReturnValue([2, vi.fn()]);
        localStorage.setItem(
            "userInterests",
            JSON.stringify(["music", "games"]),
        );

        render(
            <MemoryRouter>
                <ProfileForm />
            </MemoryRouter>,
        );

        await user.type(screen.getByLabelText(/Имя/i), "Иван");
        await user.type(screen.getByLabelText(/Фамилия/i), "Иванов");

        const submitButton = screen.getByRole("button", { name: "Готово" });
        await user.click(submitButton);

        await waitFor(() => {
            expect(saveUserProfileMock).toHaveBeenCalled();
        });

        expect(localStorage.getItem("userInterests")).toBeNull();
        expect(navigateMock).toHaveBeenCalledWith("/");
    });
});
