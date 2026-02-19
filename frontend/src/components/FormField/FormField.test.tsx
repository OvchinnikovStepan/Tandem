import { render, screen } from "@testing-library/react";
import { useEffect } from "react";
import { FormProvider, useForm } from "react-hook-form";
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

  it("reads field error from react-hook-form context", () => {
    function TestForm() {
      const methods = useForm<{ firstName: string }>({
        defaultValues: { firstName: "" },
        mode: "onSubmit",
      });

      useEffect(() => {
        methods.setError("firstName", { message: "Обязательное поле" });
      }, [methods]);

      return (
        <FormProvider {...methods}>
          <FormField label="Имя" name="firstName">
            <input type="text" />
          </FormField>
        </FormProvider>
      );
    }

    render(<TestForm />);

    expect(screen.getByText("Обязательное поле")).toBeInTheDocument();
  });
});
