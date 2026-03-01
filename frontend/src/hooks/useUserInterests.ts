import { useCallback, useState } from "react";
import { createCustomInterest, saveUserInterests } from "@/api/interests.ts";
import type { Interest } from "@/types/interests.ts";
import { useAtom } from "jotai";
import { selectedInterestsAtom } from "@/modules/Questionnaire/atoms/interestsAtoms.ts";

export function useUserInterests(userId?: string) {
    const [selectedInterests, setSelectedInterests] = useAtom(
        selectedInterestsAtom,
    );
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Сохранение интересов
    const saveInterests = useCallback(
        async (interests: Interest[]) => {
            if (!userId) {
                setError("User ID is required");
                return;
            }

            setIsSaving(true);
            setError(null);

            try {
                await saveUserInterests(userId, interests);
            } catch (err) {
                setError(
                    err instanceof Error ? err.message : "Ошибка сохранения",
                );
                throw err;
            } finally {
                setIsSaving(false);
            }
        },
        [userId],
    );

    // Добавление/удаление интереса у пользователя
    const toggleInterest = useCallback(
        (interest: Interest) => {
            // setSelectedInterests((prev) =>
            //     prev.includes(interest)
            //         ? prev.filter((name) => name !== interest)
            //         : [...prev, interest]
            // );
            setSelectedInterests((prev) =>
                prev.some(
                    (someInterest) =>
                        someInterest.id.toLowerCase() ===
                        interest.id.toLowerCase(),
                )
                    ? prev.filter(
                          (someInterest) =>
                              someInterest.id.toLowerCase() !==
                              interest.id.toLowerCase(),
                      )
                    : [...prev, interest],
            );
            console.log(
                `${selectedInterests.map((interest) => interest.name)}`,
            );
        },
        [setSelectedInterests, selectedInterests],
    );

    // Создание пользовательского интереса
    const createInterest = useCallback(
        async (customInterestName: string) => {
            try {
                const newInterest =
                    await createCustomInterest(customInterestName);
                toggleInterest(newInterest);
                return newInterest;
            } catch (err) {
                setError(
                    err instanceof Error
                        ? err.message
                        : "Ошибка создания интереса",
                );
                throw err;
            }
        },
        [toggleInterest],
    );

    return {
        isSaving,
        error,
        toggleInterest,
        createInterest,
        saveInterests,
        setSelectedInterests,
    };
}
