import { render, screen } from "@testing-library/react";
import { FormField } from "./FormField";

describe("FormField", () => {
  it("renders required marker and error text", () => {
    render(
      <FormField label="Имя" required error="Ошибка">
        <input type="text" />
      </FormField>
    );

    expect(screen.getByText("*")).toBeInTheDocument();
    expect(screen.getByText("Ошибка")).toBeInTheDocument();
  });
});
