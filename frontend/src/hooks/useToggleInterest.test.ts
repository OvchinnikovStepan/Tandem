import { describe, expect, it, type Mock, vi } from "vitest";
import { useToggleInterest } from "./useToggleInterest";
import { useAtom } from "jotai";
import type { Interest } from "@/types/interests";

vi.mock("jotai", () => ({
    useAtom: vi.fn(),
}));

describe("useToggleInterest", () => {
    it("добавляет интерес, если его ещё нет в списке", () => {
        const setSelectedInterests = vi.fn();
        (useAtom as unknown as Mock).mockReturnValue([
            [],
            setSelectedInterests,
        ]);

        const { toggleInterest } = useToggleInterest({} as never);
        const interest: Interest = { id: "music", name: "Музыка" };

        toggleInterest(interest);

        const updater = setSelectedInterests.mock.calls[0][0] as (
            prev: Interest[],
        ) => Interest[];
        expect(updater([])).toEqual([interest]);
    });

    it("удаляет интерес по id без учета регистра", () => {
        const setSelectedInterests = vi.fn();
        (useAtom as unknown as Mock).mockReturnValue([
            [{ id: "Music", name: "Музыка" }],
            setSelectedInterests,
        ]);

        const { toggleInterest } = useToggleInterest({} as never);
        toggleInterest({ id: "music", name: "Музыка" });

        const updater = setSelectedInterests.mock.calls[0][0] as (
            prev: Interest[],
        ) => Interest[];
        expect(updater([{ id: "Music", name: "Музыка" }])).toEqual([]);
    });

    it("корректно проверяет выбранные интересы", () => {
        (useAtom as unknown as Mock).mockReturnValue([
            [{ id: "music", name: "Музыка" }],
            vi.fn(),
        ]);

        const { isSelected, isAllSelected } = useToggleInterest({} as never);
        const music: Interest = { id: "Music", name: "Музыка" };
        const games: Interest = { id: "games", name: "Игры" };

        expect(isSelected(music)).toBe(true);
        expect(isSelected(games)).toBe(false);
        expect(isAllSelected([music])).toBe(true);
        expect(isAllSelected([music, games])).toBe(false);
    });
});
