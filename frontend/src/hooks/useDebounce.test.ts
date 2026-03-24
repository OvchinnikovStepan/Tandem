import { act, renderHook } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { useDebounce } from "./useDebounce";

describe("useDebounce", () => {
    it("возвращает начальное значение сразу", () => {
        const { result } = renderHook(() => useDebounce("start", 300));
        expect(result.current).toBe("start");
    });

    it("обновляет значение только после задержки", () => {
        vi.useFakeTimers();
        const { result, rerender } = renderHook(
            ({ value, delay }) => useDebounce(value, delay),
            { initialProps: { value: "first", delay: 300 } },
        );

        rerender({ value: "second", delay: 300 });
        expect(result.current).toBe("first");

        act(() => {
            vi.advanceTimersByTime(299);
        });
        expect(result.current).toBe("first");

        act(() => {
            vi.advanceTimersByTime(1);
        });
        expect(result.current).toBe("second");

        vi.useRealTimers();
    });

    it("сбрасывает таймер при быстром изменении значения", () => {
        vi.useFakeTimers();
        const { result, rerender } = renderHook(
            ({ value, delay }) => useDebounce(value, delay),
            { initialProps: { value: "a", delay: 200 } },
        );

        rerender({ value: "ab", delay: 200 });
        act(() => {
            vi.advanceTimersByTime(150);
        });

        rerender({ value: "abc", delay: 200 });
        act(() => {
            vi.advanceTimersByTime(199);
        });
        expect(result.current).toBe("a");

        act(() => {
            vi.advanceTimersByTime(1);
        });
        expect(result.current).toBe("abc");

        vi.useRealTimers();
    });
});
