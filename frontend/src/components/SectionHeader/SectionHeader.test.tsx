import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { SectionHeader } from "./SectionHeader";

describe("SectionHeader", () => {
    it("renders link when linkText and linkHref provided", () => {
        render(
            <MemoryRouter>
                <SectionHeader
                    title="Заголовок"
                    linkText="Подробнее"
                    linkHref="/more"
                />
            </MemoryRouter>,
        );

        const link = screen.getByRole("link", { name: "Подробнее" });
        expect(link).toHaveAttribute("href", "/more");
    });

    it("does not render link when link props missing", () => {
        render(
            <MemoryRouter>
                <SectionHeader title="Заголовок" />
            </MemoryRouter>,
        );

        expect(screen.queryByRole("link")).toBeNull();
    });
});
