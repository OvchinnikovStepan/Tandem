export interface QuestionnaireResponse {
    questionId: string;
    answer: string | string[];
}

export interface OnboardingPayload {
    responses: QuestionnaireResponse[];
}

export const QUESTION_IDS = {
    FIRST_NAME: "22222222-2222-2222-2222-222222222221",
    LAST_NAME: "22222222-2222-2222-2222-222222222222",
    CITY: "22222222-2222-2222-2222-222222222223",
    BIRTH_DATE: "22222222-2222-2222-2222-222222222224",
    GENDER: "22222222-2222-2222-2222-222222222225",
    INTERESTS: "22222222-2222-2222-2222-222222222226",
} as const;
