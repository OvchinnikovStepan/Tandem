import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfileEditCareer from "./ProfileEditCareer";

const getFieldContainer = (label: string) => {
    const labelEl = screen.getByText(label);
    return labelEl.closest("div") as HTMLElement;
};

describe("ProfileEditCareer", () => {
    it("updates fields and calls onSave", async () => {
        const user = userEvent.setup();
        const onSave = vi.fn();

        render(<ProfileEditCareer onSave={onSave} onBack={vi.fn()} />);

        const workplaceField = getFieldContainer("Место работы:");
        const workplaceInput = within(workplaceField).getByRole("textbox");
        await user.type(workplaceInput, "Tandem");

        const startYearField = getFieldContainer("Год начала работы:");
        const startYearSelect = within(startYearField).getByRole("combobox");
        await user.selectOptions(startYearSelect, "2024");

        const saveButton = screen.getByRole("button", { name: "Сохранить" });
        await user.click(saveButton);

        expect(onSave).toHaveBeenCalledTimes(1);
        expect(onSave).toHaveBeenCalledWith(
            expect.objectContaining({ workplace: "Tandem", startYear: "2024" }),
        );
    });
});
