import { renderHook } from "@testing-library/react";
import { describe, expect, it, type Mock, vi } from "vitest";
import { useSubmitQuestionnaire } from "./useSubmitQuestionnaire";
import { useMutation } from "@tanstack/react-query";
import { useAtomValue, useSetAtom } from "jotai";
import { useNavigate } from "react-router";
import { completeOnboarding } from "../api/completeOnbording";
import { QUESTION_IDS } from "../constants/constants";
import { RESET } from "jotai/utils";

vi.mock("@tanstack/react-query", () => ({
    useMutation: vi.fn(),
}));

vi.mock("jotai", async (importOriginal) => {
    const actual = await importOriginal<typeof import("jotai")>();
    return {
        ...actual,
        useAtomValue: vi.fn(),
        useSetAtom: vi.fn(),
    };
});

vi.mock("react-router", async (importOriginal) => {
    const actual = await importOriginal<typeof import("react-router")>();
    return {
        ...actual,
        useNavigate: vi.fn(),
    };
});

vi.mock("../api/completeOnbording.ts", () => ({
    completeOnboarding: vi.fn(),
}));

describe("useSubmitQuestionnaire", () => {
    it("формирует корректный payload и вызывает completeOnboarding", async () => {
        const resetInterests = vi.fn();
        const navigate = vi.fn();
        const interests = [
            { id: "music", name: "Музыка" },
            { id: "games", name: "Игры" },
        ];

        (useAtomValue as unknown as Mock).mockReturnValue(interests);
        (useSetAtom as unknown as Mock).mockReturnValue(resetInterests);
        (useNavigate as unknown as Mock).mockReturnValue(navigate);
        (completeOnboarding as unknown as Mock).mockResolvedValue(undefined);

        let mutationFn:
            | ((profileData: {
                  firstName: string;
                  lastName: string;
                  city: string;
                  birthDate: string;
                  gender: string;
              }) => Promise<void>)
            | undefined;
        let onSuccess: (() => void) | undefined;

        (useMutation as unknown as Mock).mockImplementation((config) => {
            mutationFn = config.mutationFn;
            onSuccess = config.onSuccess;
            return { mutate: vi.fn(), isPending: false, error: null };
        });

        renderHook(() => useSubmitQuestionnaire());

        await mutationFn?.({
            firstName: "Иван",
            lastName: "Иванов",
            city: "",
            birthDate: "22/05/2001",
            gender: "male",
        });

        expect(completeOnboarding).toHaveBeenCalledWith({
            responses: [
                { questionId: QUESTION_IDS.FIRST_NAME, answer: "Иван" },
                { questionId: QUESTION_IDS.LAST_NAME, answer: "Иванов" },
                { questionId: QUESTION_IDS.BIRTH_DATE, answer: "2001-05-22" },
                { questionId: QUESTION_IDS.GENDER, answer: "male" },
                {
                    questionId: QUESTION_IDS.INTERESTS,
                    answer: ["music", "games"],
                },
            ],
        });

        onSuccess?.();

        expect(resetInterests).toHaveBeenCalledWith(RESET);
        expect(navigate).toHaveBeenCalledWith("/profile", { replace: true });
    });

    it("не отправляет пустые необязательные поля", async () => {
        (useAtomValue as unknown as Mock).mockReturnValue([]);
        (useSetAtom as unknown as Mock).mockReturnValue(vi.fn());
        (useNavigate as unknown as Mock).mockReturnValue(vi.fn());
        (completeOnboarding as unknown as Mock).mockResolvedValue(undefined);

        let mutationFn:
            | ((profileData: {
                  firstName: string;
                  lastName: string;
                  city: string;
                  birthDate: string;
                  gender: string;
              }) => Promise<void>)
            | undefined;

        (useMutation as unknown as Mock).mockImplementation((config) => {
            mutationFn = config.mutationFn;
            return { mutate: vi.fn(), isPending: false, error: null };
        });

        renderHook(() => useSubmitQuestionnaire());

        await mutationFn?.({
            firstName: "Петр",
            lastName: "Петров",
            city: "",
            birthDate: "",
            gender: "no-select",
        });

        expect(completeOnboarding).toHaveBeenCalledWith({
            responses: [
                { questionId: QUESTION_IDS.FIRST_NAME, answer: "Петр" },
                { questionId: QUESTION_IDS.LAST_NAME, answer: "Петров" },
            ],
        });
    });
});
