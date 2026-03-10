import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ProfileTabs, { TabType } from "./ProfileTabs";

describe("ProfileTabs", () => {
  it("calls onTabChange when a tab is clicked", async () => {
    const user = userEvent.setup();
    const onTabChange = vi.fn();

    render(
      <ProfileTabs activeTab={"posts" as TabType} onTabChange={onTabChange} />
    );

    const savedTab = screen.getByRole("button", { name: "сохраненное" });
    await user.click(savedTab);

    expect(onTabChange).toHaveBeenCalledTimes(1);
    expect(onTabChange).toHaveBeenCalledWith("saved");
  });
});
