import { renderHook } from "@testing-library/react";
import { describe, expect, it, type Mock, vi } from "vitest";
import { useDefaultInterests } from "./useDefaultInterests";
import { useSuspenseQuery } from "@tanstack/react-query";

vi.mock("@tanstack/react-query", () => ({
    useSuspenseQuery: vi.fn(),
}));

describe("useDefaultInterests", () => {
    it("возвращает список интересов из useSuspenseQuery", () => {
        const defaultInterests = [{ id: "music-ru", name: "Музыка" }];
        (useSuspenseQuery as unknown as Mock).mockReturnValue({
            data: defaultInterests,
        });

        const { result } = renderHook(() => useDefaultInterests("ru"));

        expect(result.current.defaultInterests).toEqual(defaultInterests);
    });

    it("запускает запрос с корректным queryKey и конфигом", () => {
        (useSuspenseQuery as unknown as Mock).mockReturnValue({ data: [] });

        renderHook(() => useDefaultInterests("en"));

        expect(useSuspenseQuery).toHaveBeenCalledWith(
            expect.objectContaining({
                queryKey: ["defaultInterests", "en"],
                staleTime: Infinity,
                gcTime: Infinity,
                queryFn: expect.any(Function),
            }),
        );
    });
});
