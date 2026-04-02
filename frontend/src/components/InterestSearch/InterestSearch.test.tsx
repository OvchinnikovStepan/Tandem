import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { InterestSearch } from "./InterestSearch";
import { useInterestSearch } from "@/hooks/useInterestSearch";
import { type Mock, vi } from "vitest";

vi.mock("@/hooks/useInterestSearch.ts", () => ({
    useInterestSearch: vi.fn(),
}));

vi.mock("@/components/InterestSearch/InterestSearchResults.tsx", () => ({
    InterestSearchResults: () => <div data-testid="interest-search-results" />,
}));

describe("InterestSearch", () => {
    const setupHookMock = (overrides?: Record<string, unknown>) => {
        (useInterestSearch as unknown as Mock).mockReturnValue({
            searchInputRef: { current: null },
            searchResultsRef: { current: null },
            addButtonRef: { current: null },
            searchQuery: "",
            setSearchQuery: vi.fn(),
            showSearchResults: false,
            searchResults: [],
            isLoading: false,
            handleSearchResultSelect: vi.fn(),
            handleAddButtonClick: vi.fn(),
            ...overrides,
        });
    };

    beforeEach(() => {
        (useInterestSearch as unknown as Mock).mockReset();
    });

    it("рендерит поле поиска и кнопку добавления", () => {
        setupHookMock();

        render(<InterestSearch interestsAtom={{} as never} />);

        expect(
            screen.getByPlaceholderText("Введите название вашего интереса..."),
        ).toBeInTheDocument();
        expect(
            screen.getByRole("button", { name: "Добавить" }),
        ).toBeInTheDocument();
    });

    it("вызывает добавление по клику на кнопку", async () => {
        const user = userEvent.setup();
        const handleAddButtonClick = vi.fn();
        setupHookMock({ handleAddButtonClick });

        render(<InterestSearch interestsAtom={{} as never} />);

        await user.click(screen.getByRole("button", { name: "Добавить" }));

        expect(handleAddButtonClick).toHaveBeenCalledTimes(1);
    });

    it("вызывает добавление по Enter в поле поиска", async () => {
        const user = userEvent.setup();
        const handleAddButtonClick = vi.fn();
        setupHookMock({ handleAddButtonClick, searchQuery: "Музыка" });

        render(<InterestSearch interestsAtom={{} as never} />);

        await user.type(
            screen.getByPlaceholderText("Введите название вашего интереса..."),
            "{enter}",
        );

        expect(handleAddButtonClick).toHaveBeenCalledTimes(1);
    });

    it("показывает результаты поиска при активном состоянии", () => {
        setupHookMock({
            showSearchResults: true,
            searchQuery: "Музыка",
            searchResults: [{ id: "music", name: "Музыка", userCount: 10 }],
        });

        render(<InterestSearch interestsAtom={{} as never} />);

        expect(
            screen.getByTestId("interest-search-results"),
        ).toBeInTheDocument();
    });
});
