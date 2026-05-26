import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfileEdit from "./ProfileEdit";

const userData = {
    id: "1",
    firstName: "U. Wu",
    lastName: "Azunyan",
    username: "azunyan_0777",
    gender: "male",
    birthDate: "",
    bio: "",
    city: "Омск",
    avatar: undefined,
};

const getFieldContainer = (label: string) => {
    const labelEl = screen.getByText(label);
    return labelEl.closest("div") as HTMLElement;
};

describe("ProfileEdit", () => {
    it("updates form and calls onSave", async () => {
        const user = userEvent.setup();
        const onSave = vi.fn();
        const onCancel = vi.fn();

        render(
            <ProfileEdit user={userData} onSave={onSave} onCancel={onCancel} />,
        );

        const nameField = getFieldContainer("Имя:");
        const nameInput = within(nameField).getByRole("textbox");
        await user.clear(nameInput);
        await user.type(nameInput, "Alex");

        const genderField = getFieldContainer("Пол:");
        const genderSelect = within(genderField).getByRole("combobox");
        await user.selectOptions(genderSelect, "female");

        const saveButton = screen.getByRole("button", { name: "Сохранить" });
        await user.click(saveButton);

        expect(onSave).toHaveBeenCalledTimes(1);
        expect(onSave).toHaveBeenCalledWith(
            expect.objectContaining({ firstName: "Alex", gender: "female" }),
        );
    });
});
