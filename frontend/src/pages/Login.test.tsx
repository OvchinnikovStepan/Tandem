import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import Login from "./Login";

vi.mock("react-router", async () => {
  const actual = await vi.importActual("react-router");
  return {
    ...actual,
    useNavigate: () => vi.fn(),
  };
});

describe("Login Page", () => {
  const renderPage = () => {
    return render(
      <MemoryRouter>
        <Login />
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

  it("рендерит форму входа", () => {
    renderPage();
    expect(screen.getByText("Вход в Tandem")).toBeInTheDocument();
  });

  it("рендерит поле email", () => {
    renderPage();
    expect(screen.getByLabelText("Email")).toBeInTheDocument();
  });

  it("рендерит поле пароля", () => {
    renderPage();
    expect(screen.getByLabelText("Пароль")).toBeInTheDocument();
  });

  it("рендерит кнопку входа", () => {
    renderPage();
    expect(screen.getByRole("button", { name: "Войти" })).toBeInTheDocument();
  });

  it("рендерит ссылку на регистрацию", () => {
    renderPage();
    expect(screen.getByText(/Нет аккаунта?/)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Зарегистрироваться" })).toBeInTheDocument();
  });

  it("рендерит ссылку восстановления пароля", () => {
    renderPage();
    expect(screen.getByRole("button", { name: "Забыли пароль?" })).toBeInTheDocument();
  });
});
