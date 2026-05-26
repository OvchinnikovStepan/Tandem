import { useAtom } from "jotai";
import { selectedInterestsAtom } from "@/modules/Questionnaire/atoms/interestsAtoms.ts";
import { useMutation } from "@tanstack/react-query";
import type { Interest } from "@/types/interests.ts";
import { createCustomInterest, saveUserInterests } from "@/api/interests.ts";

export function HOOKSFORFUTUREDONTCOMMIT(userId?: string) {
    const [, setSelectedInterests] = useAtom(selectedInterestsAtom);

    // Сохранение интересов
    const {
        mutate: saveInterests,
        isPending: isSaving,
        error,
    } = useMutation({
        mutationFn: (interests: Interest[]) => {
            if (!userId) throw new Error("User ID is required");
            return saveUserInterests(userId, interests);
        },
        onError: (err) => {
            console.error(
                err instanceof Error ? err.message : "Ошибка сохранения",
            );
        },
    });

    // Добавление/удаление интереса у пользователя
    const toggleInterest = (interest: Interest) => {
        setSelectedInterests((prev) =>
            prev.some(
                (someInterest) =>
                    someInterest.id.toLowerCase() === interest.id.toLowerCase(),
            )
                ? prev.filter(
                      (someInterest) =>
                          someInterest.id.toLowerCase() !==
                          interest.id.toLowerCase(),
                  )
                : [...prev, interest],
        );
    };

    // Создание пользовательского интереса
    // (будет окончательно реализовано в будущем через редактирование профиля,
    // пока работает как псевдо-создание интереса)
    const {
        mutate: createInterest,
        isPending: isCreating,
        error: createError,
    } = useMutation({
        mutationFn: (customInterestName: string) =>
            createCustomInterest(customInterestName),
        onSuccess: (newInterest: Interest) => {
            toggleInterest(newInterest);
        },
        onError: (err) => {
            console.error(
                err instanceof Error ? err.message : "Ошибка создания интереса",
            );
        },
    });

    return {
        isSaving,
        error,
        toggleInterest,
        createInterest,
        saveInterests,
        setSelectedInterests,
        isCreating,
        createError,
    };
}
