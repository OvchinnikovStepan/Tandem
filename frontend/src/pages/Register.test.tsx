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

describe("Register Page", () => {
  beforeEach(() => {
    mockNavigate.mockClear();
  });

  const renderPage = () => {
    return render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );
  };

  it("рендерит Header", () => {
    renderPage();
    expect(screen.getByAltText("Tandem")).toBeInTheDocument();
  });

  it("рендерит HeroBlock", () => {
    renderPage();
    expect(screen.getByAltText("Illustration")).toBeInTheDocument();
  });

  it("рендерит заголовок первого шага", () => {
    renderPage();
    expect(screen.getByText("Create account")).toBeInTheDocument();
  });

  it("показывает 'Шаг 1 из 2' на первом шаге", () => {
    renderPage();
    expect(screen.getByText("Шаг 1 из 2")).toBeInTheDocument();
  });

  it("рендерит поле email на первом шаге", () => {
    renderPage();
    expect(screen.getByLabelText("Email")).toBeInTheDocument();
  });

  it("рендерит кнопку 'Далее'", () => {
    renderPage();
    expect(screen.getByRole("button", { name: "Далее" })).toBeInTheDocument();
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

  it("переходит ко второму шагу после валидации email", async () => {
    const user = userEvent.setup();
    renderPage();
    
    const emailInput = screen.getByLabelText("Email");
    await user.type(emailInput, "test@example.com");
    await user.click(screen.getByRole("button", { name: "Далее" }));
    
    expect(await screen.findByText("Придумайте пароль")).toBeInTheDocument();
    expect(screen.getByText("Шаг 2 из 2")).toBeInTheDocument();
  });

  it("показывает поля пароля на втором шаге", async () => {
    const user = userEvent.setup();
    renderPage();
    
    const emailInput = screen.getByLabelText("Email");
    await user.type(emailInput, "test@example.com");
    await user.click(screen.getByRole("button", { name: "Далее" }));
    
    expect(await screen.findByPlaceholderText("Пароль")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Повторите пароль")).toBeInTheDocument();
  });

  it("прогресс-бар отображается правильно", () => {
    const { container } = renderPage();
    const progressBars = container.querySelectorAll('[class*="h-1"]');
    expect(progressBars.length).toBeGreaterThan(0);
  });
});
