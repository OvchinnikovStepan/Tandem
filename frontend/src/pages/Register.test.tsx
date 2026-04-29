import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import Register from "./Register";

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

vi.mock("@/api/auth", () => ({
  registerPhone: vi.fn(async () => ({ verificationId: "vid-1" })),
  verifyPhone: vi.fn(async () => ({ verified: true })),
  registerEmail: vi.fn(async () => ({
    userId: "uid-1",
    accessToken: "access",
    refreshToken: "refresh",
    passwordStrength: "GOOD",
  })),
}));

describe("Register Page", () => {
  beforeEach(() => {
    mockNavigate.mockClear();
    window.localStorage.clear();
  });

  const renderPage = () =>
    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>,
    );

  it("рендерит Header", () => {
    renderPage();
    expect(screen.getByAltText("Tandem")).toBeInTheDocument();
  });

  it("рендерит HeroBlock", () => {
    renderPage();
    expect(screen.getByAltText("Illustration")).toBeInTheDocument();
  });

  it("рендерит заголовок первого шага (телефон)", () => {
    renderPage();
    expect(screen.getByText("Введите номер телефона")).toBeInTheDocument();
  });

  it("показывает 'Шаг 1 из 3' на первом шаге", () => {
    renderPage();
    expect(screen.getByText("Шаг 1 из 3")).toBeInTheDocument();
  });

  it("рендерит поле phoneNumber на первом шаге", () => {
    renderPage();
    expect(screen.getByLabelText("Номер телефона")).toBeInTheDocument();
  });

  it("рендерит ссылку на вход", () => {
    renderPage();
    expect(screen.getByText(/Уже есть аккаунт?/)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Войти" })).toBeInTheDocument();
  });

  it("навигирует к /login при клике на 'Войти'", async () => {
    const user = userEvent.setup();
    renderPage();
    await user.click(screen.getByRole("button", { name: "Войти" }));
    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  it("переходит ко второму шагу после ввода телефона", async () => {
    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText("Номер телефона"), "+79991234567");
    await user.click(screen.getByRole("button", { name: "Далее" }));

    expect(await screen.findByText("Введите код")).toBeInTheDocument();
    expect(screen.getByText("Шаг 2 из 3")).toBeInTheDocument();
  });
});
