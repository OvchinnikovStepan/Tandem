import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfileEditInterests from "./ProfileEditInterests";

describe("ProfileEditInterests", () => {
  it("removes interest and calls onSave", async () => {
    const user = userEvent.setup();
    const onSave = vi.fn();

    render(
      <ProfileEditInterests
        interests={[
          { id: "1", label: "Баскетбол" },
          { id: "2", label: "Футбол" },
        ]}
        onSave={onSave}
        onBack={vi.fn()}
      />
    );

    const removeButtons = document.querySelectorAll('button[type="button"]');
    await user.click(removeButtons[0]);

    const saveButton = screen.getByRole("button", { name: "Сохранить" });
    await user.click(saveButton);

    expect(onSave).toHaveBeenCalledTimes(1);
    expect(onSave).toHaveBeenCalledWith([{ id: "2", label: "Футбол" }]);
  });
});
