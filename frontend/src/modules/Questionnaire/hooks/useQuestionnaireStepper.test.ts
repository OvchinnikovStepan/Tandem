import { describe, expect, it, type Mock, vi } from "vitest";
import { useAtom } from "jotai";
import { useQuestionnaireStepper } from "@/modules/Questionnaire";

vi.mock("jotai", async (importOriginal) => {
    const actual = await importOriginal<typeof import("jotai")>();
    return {
        ...actual,
        useAtom: vi.fn(),
    };
});

describe("useQuestionnaireStepper", () => {
    it("возвращает текущий шаг анкеты из атома", () => {
        (useAtom as unknown as Mock).mockReturnValue([2, vi.fn()]);

        const result = useQuestionnaireStepper();

        expect(result).toEqual({ selectedForm: 2 });
    });
});
