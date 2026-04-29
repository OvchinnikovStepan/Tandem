import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router";
import ForgotPassword from "./ForgotPassword";

const mockNavigate = vi.fn();
vi.mock("react-router", async () => {
  const actual = await vi.importActual("react-router");
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe("ForgotPassword Page", () => {
  beforeEach(() => {
    mockNavigate.mockClear();
  });

  const renderPage = () => {
    return render(
      <MemoryRouter>
        <ForgotPassword />
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
    expect(screen.getByText("Восстановить пароль")).toBeInTheDocument();
  });

  it("рендерит описание первого шага", () => {
    renderPage();
    expect(screen.getByText("Введите ваш email для подтверждения")).toBeInTheDocument();
  });

  it("рендерит поле email на первом шаге", () => {
    renderPage();
    expect(screen.getByPlaceholderText("email")).toBeInTheDocument();
  });

  it("рендерит кнопку 'Подтвердить email'", () => {
    renderPage();
    expect(screen.getByRole("button", { name: "Подтвердить email" })).toBeInTheDocument();
  });

  it("переходит ко второму шагу после отправки email", async () => {
    const user = userEvent.setup();
    renderPage();
    
    const emailInput = screen.getByPlaceholderText("email");
    await user.type(emailInput, "test@example.com");
    await user.click(screen.getByRole("button", { name: "Подтвердить email" }));
    
    expect(await screen.findByText("Введите код")).toBeInTheDocument();
  });

  it("показывает email на втором шаге", async () => {
    const user = userEvent.setup();
    renderPage();
    
    await user.type(screen.getByPlaceholderText("email"), "test@example.com");
    await user.click(screen.getByRole("button", { name: "Подтвердить email" }));
    
    expect(await screen.findByText("test@example.com")).toBeInTheDocument();
  });

  it("показывает 6 полей для кода на втором шаге", async () => {
    const user = userEvent.setup();
    renderPage();
    
    await user.type(screen.getByPlaceholderText("email"), "test@example.com");
    await user.click(screen.getByRole("button", { name: "Подтвердить email" }));
    
    await screen.findByText("Введите код");
    const codeInputs = screen.getAllByRole("textbox");
    expect(codeInputs.length).toBe(6);
  });

  it("показывает кнопку назад на втором шаге", async () => {
    const user = userEvent.setup();
    renderPage();
    
    await user.type(screen.getByPlaceholderText("email"), "test@example.com");
    await user.click(screen.getByRole("button", { name: "Подтвердить email" }));
    
    await screen.findByText("Введите код");
    const buttons = screen.getAllByRole("button");
    const backButton = buttons.find(btn => btn.querySelector("svg"));
    expect(backButton).toBeInTheDocument();
  });

  it("переходит к третьему шагу после ввода кода", async () => {
    const user = userEvent.setup();
    renderPage();
    
    await user.type(screen.getByPlaceholderText("email"), "test@example.com");
    await user.click(screen.getByRole("button", { name: "Подтвердить email" }));
    
    await screen.findByText("Введите код");
    const codeInputs = screen.getAllByRole("textbox");
    
    for (let i = 0; i < 6; i++) {
      await user.type(codeInputs[i], String(i + 1));
    }
    
    await user.click(screen.getByRole("button", { name: "Подтвердить код" }));
    
    expect(await screen.findByText("Создать новый пароль")).toBeInTheDocument();
  });
});
