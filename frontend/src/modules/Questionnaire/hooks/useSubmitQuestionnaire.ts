import { useMutation } from "@tanstack/react-query";
import { useAtomValue, useSetAtom } from "jotai";
import { useNavigate } from "react-router";
import { selectedInterestsAtom } from "./../atoms/interestsAtoms";
import { completeOnboarding } from "./../api/completeOnbording.ts";
import { QUESTION_IDS } from "./../constants/constants";
import type { ProfileFormValues } from "@/modules/Questionnaire/schemas/profileSchema";
import { RESET } from "jotai/utils";
import type { Interest } from "@/types/interests.ts";

const formatToISO = (value: string) => {
    const [day, month, year] = value.split("/");
    return `${year}-${month}-${day}`;
};

const mapProfileToPayload = (
    data: ProfileFormValues,
    interests: Interest[],
) => ({
    responses: [
        { questionId: QUESTION_IDS.FIRST_NAME, answer: data.firstName },
        { questionId: QUESTION_IDS.LAST_NAME, answer: data.lastName },
        { questionId: QUESTION_IDS.CITY, answer: data.city ?? "" },
        {
            questionId: QUESTION_IDS.BIRTH_DATE,
            answer: data.birthDate ? formatToISO(data.birthDate) : "",
        },
        {
            questionId: QUESTION_IDS.GENDER,
            answer: data.gender === "no-select" ? "" : data.gender,
        },
        {
            questionId: QUESTION_IDS.INTERESTS,
            answer: interests.map((i) => i.id),
        },
    ].filter((r) =>
        // пустые необязательные поля не отправляются
        Array.isArray(r.answer) ? r.answer.length > 0 : r.answer !== "",
    ),
});

export function useSubmitQuestionnaire() {
    const navigate = useNavigate();
    const selectedInterests = useAtomValue(selectedInterestsAtom);
    const resetInterests = useSetAtom(selectedInterestsAtom);

    const { mutate, isPending, error } = useMutation({
        mutationFn: (profileData: ProfileFormValues) => {
            const payload = mapProfileToPayload(profileData, selectedInterests);
            return completeOnboarding(payload);
        },
        onSuccess: () => {
            resetInterests(RESET);
            navigate("/profile", { replace: true });
        },
    });

    return { saveQuestionnaire: mutate, isPending, error };
}
