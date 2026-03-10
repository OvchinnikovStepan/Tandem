import { render } from "@testing-library/react";
import { Card, CardContent, CardFooter, CardHeader } from "./Card";

describe("Card", () => {
  it("renders header/content/footer", () => {
    const { getByText } = render(
      <Card>
        <CardHeader>Header</CardHeader>
        <CardContent>Content</CardContent>
        <CardFooter>Footer</CardFooter>
      </Card>
    );

    expect(getByText("Header")).toBeInTheDocument();
    expect(getByText("Content")).toBeInTheDocument();
    expect(getByText("Footer")).toBeInTheDocument();
  });

  it("applies default variant classes", () => {
    const { container } = render(<Card variant="default">X</Card>);
    const root = container.firstElementChild as HTMLElement;
    expect(root.className).toContain("border");
  });
});
