import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfileHeader from "./ProfileHeader";

describe("ProfileHeader", () => {
  it("calls onSearch on input change", async () => {
    const user = userEvent.setup();
    const onSearch = vi.fn();

    render(<ProfileHeader onSearch={onSearch} />);

    const input = screen.getByPlaceholderText("Поиск по вашим постам...");
    await user.type(input, "abc");

    expect(onSearch).toHaveBeenCalled();
    expect(onSearch).toHaveBeenLastCalledWith("abc");
  });

  it("calls onCreatePost on button click", async () => {
    const user = userEvent.setup();
    const onCreatePost = vi.fn();

    render(<ProfileHeader onCreatePost={onCreatePost} />);

    const button = screen.getByRole("button", { name: "Создать пост" });
    await user.click(button);

    expect(onCreatePost).toHaveBeenCalledTimes(1);
  });
});
